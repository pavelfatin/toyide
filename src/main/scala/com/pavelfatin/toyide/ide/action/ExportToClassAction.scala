/*
 * Copyright (C) 2011 Pavel Fatin <http://pavelfatin.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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