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
import com.pavelfatin.toyide.editor._

private class Complete(document: Document, terminal: Terminal, data: Data,
                       adviser: Adviser, history: History) extends AnAction {
  def keys = List("ctrl pressed SPACE")

  def apply() {
    terminal.selection = None
    terminal.highlights = Seq.empty
    val label = Adviser.Anchor
    document.insert(terminal.offset, label)
    data.compute()
    val structure = data.structure
    document.remove(terminal.offset, terminal.offset + label.length)
    for (root <- structure;
         anchor <- root.elements.find(it => it.isLeaf && it.span.text.contains(label))) {
      val variants = adviser.variants(root, anchor)
      val query = document.text(anchor.span.begin, terminal.offset)
      val filtered = variants.filter(_.content.startsWith(query))
      filtered match {
        case Seq() =>
        case Seq(single) => history.recording(document, terminal) {
          insert(single, query)
        }
        case multiple => terminal.choose(multiple, query) { it =>
          history.recording(document, terminal) {
            insert(it, query)
          }
        }
      }
    }
  }

  private def insert(variant: Variant, query: String) {
    terminal.insertInto(document, variant.content.stripPrefix(query))
    terminal.offset += variant.shift
  }
}