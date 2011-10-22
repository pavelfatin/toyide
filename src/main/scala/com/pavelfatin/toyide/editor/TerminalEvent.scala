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

package com.pavelfatin.toyide.editor

import com.pavelfatin.toyide.Interval

sealed trait TerminalEvent {
  def undo(terminal: Terminal)

  def redo(terminal: Terminal)
}

case class CaretMovement(from: Int, to: Int) extends TerminalEvent {
  def undo(terminal: Terminal) {
    terminal.offset = from
  }

  def redo(terminal: Terminal) {
    terminal.offset = to
  }
}

case class SelectionChange(from: Option[Interval], to: Option[Interval]) extends TerminalEvent {
  def undo(terminal: Terminal) {
    terminal.selection = from
  }

  def redo(terminal: Terminal) {
    terminal.selection = to
  }
}

case class HighlightsChange(from: Seq[Interval], to: Seq[Interval]) extends TerminalEvent {
  def undo(terminal: Terminal) {
    terminal.highlights = from
  }

  def redo(terminal: Terminal) {
    terminal.highlights = to
  }
}

case class HoverChange(from: Option[Int], to: Option[Int]) extends TerminalEvent {
  def undo(terminal: Terminal) {
    terminal.hover = from
  }

  def redo(terminal: Terminal) {
    terminal.hover = to
  }
}