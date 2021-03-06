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

private class MoveLineDown(document: Document, terminal: Terminal) extends AnAction with Repeater {
  repeat(document, terminal)

  def keys = List("shift ctrl pressed DOWN")

  override def enabled = document.lineNumberOf(terminal.offset) < document.linesCount - 1

  def apply() {
    val location = document.toLocation(terminal.offset)

    val source = document.intervalOf(location.line)
    val target = document.intervalOf(location.line + 1)

    val targetText = document.text(target)

    terminal.selection = None

    document.replace(target, document.text(source))
    document.replace(source, targetText)

    document.toOffset(location.copy(line = location.line + 1)).foreach(terminal.offset = _)
  }
}