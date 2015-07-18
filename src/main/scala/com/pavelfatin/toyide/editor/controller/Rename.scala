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