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

package com.pavelfatin.toyide.editor.controller

import java.awt.datatransfer.StringSelection
import java.awt.Toolkit
import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.editor.{AnAction, Terminal}

private class Cut(document: Document, terminal: Terminal) extends AnAction {
  def keys = List("ctrl pressed X", "shift pressed DELETE")

  def apply() {
    if(terminal.selection.isEmpty)
      terminal.selection = Some(terminal.currentLineIntervalIn(document))

    terminal.selection.foreach { s =>
      val text = new StringSelection(document.text(s))
      Toolkit.getDefaultToolkit.getSystemClipboard.setContents(text, null)
      terminal.insertInto(document, "")
    }
  }
}