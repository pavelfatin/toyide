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