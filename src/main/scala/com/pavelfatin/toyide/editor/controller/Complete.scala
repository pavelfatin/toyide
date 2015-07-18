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