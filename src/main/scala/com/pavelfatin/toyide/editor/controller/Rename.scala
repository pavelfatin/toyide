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

import com.pavelfatin.toyide.document.{Bias, Document}
import com.pavelfatin.toyide.editor.{AnAction, History, Data, Terminal}

private class Rename(document: Document, terminal: Terminal, data: Data, history: History) extends AnAction {
  def keys = List("shift pressed F6")

  def apply() {
    data.compute()
    val leafs = data.connectedLeafsFor(terminal.offset)
    if (leafs.nonEmpty) {
      terminal.selection = None
      terminal.highlights = leafs.map(_.span.interval)
      val id = leafs.head.span.text
      terminal.edit(id, "Rename") {
        case Some(text) =>
          terminal.highlights = Seq.empty
          history.recording(document, terminal) {
            val anchor = document.createAnchorAt(terminal.offset, Bias.Right)
            leafs.map(_.span.interval).sortBy(_.begin).reverse.foreach(document.replace(_, text))
            terminal.offset = anchor.offset
            anchor.dispose()
          }
        case None =>
          terminal.highlights = Seq.empty
      }
    }
  }
}