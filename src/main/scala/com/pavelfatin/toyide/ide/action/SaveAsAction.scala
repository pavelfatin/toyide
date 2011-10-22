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

import javax.swing.filechooser.FileNameExtensionFilter
import java.io.File
import javax.swing.KeyStroke
import com.pavelfatin.toyide.ide.EditorTab
import swing.{Dialog, Component, FileChooser, Action}

class SaveAsAction(title0: String, mnemonic0: Char, shortcut: String,
                           parent: Component, tab: EditorTab) extends Action(title0) {
  mnemonic = mnemonic0

  accelerator = Some(KeyStroke.getKeyStroke(shortcut))

  def apply() {
    SaveAsAction.performOn(tab, parent)
  }
}

private object SaveAsAction {
  def performOn(tab: EditorTab, parent: Component, selection: Option[File] = None) {
    val chooser = new FileChooser()
    chooser.title = "Save As"
    chooser.fileFilter = new FileNameExtensionFilter(tab.fileType.name, tab.fileType.extension)
    selection.foreach(chooser.selectedFile = _)
    chooser.showSaveDialog(parent) match {
      case FileChooser.Result.Approve =>
        val file = toTarget(chooser.selectedFile, tab.fileType.extension)
        if (file.exists) {
          val result = Dialog.showConfirmation(parent,
            "File '%s' already exists.\nDo you want to overwrite it?".format(file.getName),
            "File already exists", Dialog.Options.YesNoCancel, Dialog.Message.Warning)
          result match {
            case Dialog.Result.Yes => doSave(file, tab)
            case Dialog.Result.No => performOn(tab, parent, Some(chooser.selectedFile))
            case Dialog.Result.Cancel =>
          }
        } else {
          doSave(file, tab)
        }
      case _ =>
    }
  }

  private def toTarget(file: File, extension: String): File = {
    val tail = ".%s".format(extension)
    val path = if (file.getName.endsWith(tail)) file.getPath else "%s%s".format(file.getPath, tail)
    new File(path)
  }

  private def doSave(file: File, tab: EditorTab) {
    IO.write(file, tab.text)
    tab.file = Some(file)
  }
}