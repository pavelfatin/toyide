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

import com.pavelfatin.toyide.formatter.Formatter
import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.editor.{AnAction, Terminal, Data}

private class Format(document: Document, terminal: Terminal, data: Data, formatter: Formatter, tabSize: Int) extends AnAction {
  def keys = List("ctrl alt pressed L")

  def apply() {
    data.compute()
    data.structure.foreach { root =>
      val text = formatter.format(root, terminal.selection, tabSize)
      terminal.offset = terminal.offset.min(text.length)
      document.text = text
    }
  }
}