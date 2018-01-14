/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pavelfatin.toyide.ide.action

import com.pavelfatin.toyide.compiler.Assembler
import com.pavelfatin.toyide.editor.Data
import swing.{Component, FileChooser, Action}
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter
import com.pavelfatin.toyide.node.Node
import java.io.{File, FileOutputStream}

class ExportToClassAction(title0: String, mnemonic0: Char, data: Data, parent: Component) extends Action(title0) {
  mnemonic = mnemonic0

  def apply() {
    data.compute()
    if (!data.hasFatalErrors) {
      data.structure.foreach { root =>
          val chooser = new FileChooser()
          chooser.title = "Export to Class"
          chooser.fileFilter = new FileNameExtensionFilter("JVM class", "class")
          chooser.showSaveDialog(parent) match {
            case FileChooser.Result.Approve => try {
              save(root, chooser.selectedFile)
            } catch {
              case e: Exception => JOptionPane.showMessageDialog(parent.peer,
                e.getMessage, "Export error", JOptionPane.ERROR_MESSAGE)
            }
            case _ =>
          }
      }
    }
  }

  private def save(root: Node, file: File) {
    val (name, path) = if (file.getName.endsWith(".class")) (file.getName.dropRight(6), file.getPath)
      else (file.getName, "%s.class".format(file.getPath))

    val bytecode = Assembler.assemble(root, name)
    val stream = new FileOutputStream(path)
    try {
      stream.write(bytecode)
      stream.flush()
    } finally {
      stream.close()
    }
  }
}