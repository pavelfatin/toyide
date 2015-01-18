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

private class ToggleLineComment(document: Document, terminal: Terminal, comment: String) extends AnAction {
  def keys = List("ctrl pressed SLASH")

  def apply() {
    val interval = terminal.currentLineIntervalIn(document)
    val line = document.text(interval)

    terminal.selection = None

    if (line.trim.startsWith(comment)) {
      val i = interval.begin + line.indexOf(comment)
      val commentInterval = Interval(i, i + comment.length)
      document.remove(commentInterval)
      if (commentInterval.touches(terminal.offset))
        terminal.offset = i
      else
        terminal.offset -= comment.length
    } else {
      document.insert(interval.begin, comment)
      if (terminal.offset > interval.begin) terminal.offset += comment.length
    }

    moveCaretDown()
  }

  private def moveCaretDown() {
    if (document.lineNumberOf(terminal.offset) < document.linesCount - 1) {
      val line = document.lineNumberOf(terminal.offset)
      val indent = terminal.offset - document.startOffsetOf(line)
      val target = document.startOffsetOf(line + 1) + indent
      terminal.offset = target.min(document.endOffsetOf(line + 1))
      terminal.selection = None
    }
  }
}