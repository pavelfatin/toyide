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

import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.editor.{AnAction, Terminal}
import com.pavelfatin.toyide.Interval

private class DuplicateLine(document: Document, terminal: Terminal) extends AnAction {
  def keys = List("ctrl pressed D")

  def apply() {
    val selection = terminal.selection
    val interval = selection.getOrElse {
      val line = document.toLocation(terminal.offset).line
      Interval(document.startOffsetOf(line), document.endOffsetOf(line))
    }
    val snippet = document.text(interval)
    val addition = if (selection.isDefined) snippet else "\n%s".format(snippet)

    document.insert(interval.end, addition)

    terminal.offset += addition.length

    for (selection <- selection) {
      terminal.selection = Some(selection.transformWith(_ + addition.length))
    }
  }
}