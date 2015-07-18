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

private class UnindentSelection(document: Document, terminal: Terminal, tabSize: Int) extends AnAction with Repeater {
  repeat(document, terminal)

  def keys = List("shift pressed TAB")

  override def enabled = terminal.selection.isDefined

  def apply() {
    terminal.selection.foreach { it =>
      val selection = if (document.toLocation(it.end).indent == 0) it.withEndShift(-1) else it
      val beginLine = document.lineNumberOf(selection.begin)
      val endLine = document.lineNumberOf(selection.end)
      val interval = Interval(document.startOffsetOf(beginLine), document.endOffsetOf(endLine))

      val text = document.text(interval)
      val replacement = text.split("\n").map(s => s.drop(tabSize.min(s.takeWhile(_.isWhitespace).length))).mkString("\n")

      val decrement = text.length - replacement.length
      terminal.offset -= decrement
      terminal.selection = Some(it.withEndShift(-decrement))

      document.replace(interval, replacement)
    }
  }
}