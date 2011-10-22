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

import com.pavelfatin.toyide.document.{Document, DocumentEvent}

class HistoryImpl extends History {
  private var toUndo = List[Action]()
  private var toRedo = List[Action]()

  private var record = false

  def recording(document: Document, terminal: Terminal)(block: => Unit) {
    if (record)
      throw new IllegalStateException("Nested recording")

    record = true
    var events = List.empty[AnyRef]

    val recorder = events ::= (_: AnyRef)

    document.onChange(recorder)
    terminal.onChange(recorder)

    block

    document.disconnect(recorder)
    terminal.disconnect(recorder)

    if(events.exists(_.isInstanceOf[DocumentEvent])) {
      toUndo ::= Action(document, terminal, events)
      toRedo = List.empty
    }

    record = false
  }

  def canUndo = toUndo.nonEmpty

  def undo() {
    if (!canUndo)
      throw new IllegalStateException("Nothing to undo")

    toUndo.headOption.foreach { action =>
      action.undo()
      toUndo = toUndo.tail
      toRedo ::= action
    }
  }

  def canRedo = toRedo.nonEmpty

  def redo() {
    if (!canRedo)
      throw new IllegalStateException("Nothing to redo")

    toRedo.headOption.foreach { action =>
      action.redo()
      toRedo = toRedo.tail
      toUndo ::= action
    }
  }

  def clear() {
    toUndo = List.empty
    toRedo = List.empty
  }

  private case class Action(document: Document, terminal: Terminal, events: List[AnyRef]) {
    def undo() {
      events.foreach {
        case it: DocumentEvent => it.undo(document)
        case it: TerminalEvent => it.undo(terminal)
      }
    }

    def redo() {
      events.reverse.foreach {
        case it: DocumentEvent => it.redo(document)
        case it: TerminalEvent => it.redo(terminal)
      }
    }
  }
}