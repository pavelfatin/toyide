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

import com.pavelfatin.toyide.node.{IdentifiedNode, Node}
import com.pavelfatin.toyide.editor.{AnAction, Terminal, Data}

private class GotoDeclaration(terminal: Terminal, data: Data) extends AnAction {
  def keys = List("ctrl pressed B")

  def apply() {
    data.compute()
    for (reference <- data.referenceAt(terminal.offset);
         target <- reference.target) {
      terminal.offset = offsetOf(target)
    }
  }

  private def offsetOf(target: Node): Int = {
    target match {
      case IdentifiedNode(id, _) => id.span.begin
      case node => node.span.begin
    }
  }
}