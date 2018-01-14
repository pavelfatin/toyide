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