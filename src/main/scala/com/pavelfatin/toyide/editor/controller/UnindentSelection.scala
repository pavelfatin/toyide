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