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

import com.pavelfatin.toyide.node.{IdentifiedNode, ReferenceNodeTarget, Node}
import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.Interval

package object controller {
  private[controller] implicit def toNodeExt(node: Node) = new NodeExt(node)

  private[controller] implicit def toDataExt(data: Data) = new DataExt(data)

  private[controller] implicit def toTerminalExt(terminal: Terminal) = new TerminalExt(terminal)

  private[controller] class DataExt(data: Data) {
    def leafAt(offset: Int) = data.structure.flatMap { root =>
      root.offsetOf(offset).flatMap(root.leafAt)
    }

    def referenceAt(offset: Int) = data.structure.flatMap { root =>
      root.offsetOf(offset).flatMap(root.referenceAt)
    }

    def identifierAt(offset: Int) = data.structure.flatMap { root =>
      root.offsetOf(offset).flatMap(root.identifierAt)
    }

    def connectedLeafsFor(offset: Int): Seq[Node] = {
      val targetNode = referenceAt(offset) collect {
        case ReferenceNodeTarget(node: IdentifiedNode) => node
      } orElse {
        identifierAt(offset)
      }
      val refs = data.structure.toList.flatMap { root =>
        root.elements.collect {
          case ref @ ReferenceNodeTarget(target) if Some(target) == targetNode => ref
        }
      }
      targetNode.flatMap(_.id).toList ::: refs.flatMap(_.source)
    }
  }

  private[controller] class NodeExt(node: Node) {
    def offsetOf(i: Int): Option[Int] = {
      if (node.span.touches(i)) Some(i - node.span.begin) else None
    }
  }

  private[controller] class TerminalExt(terminal: Terminal) {
    def currentLineIntervalIn(document: Document) = {
      val line = document.lineNumberOf(terminal.offset)
      val begin = document.startOffsetOf(line)
      val postfix = 1.min(document.linesCount - line - 1)
      val end = document.endOffsetOf(line) + postfix
      Interval(begin, end)
    }

    def insertInto(document: Document, s: String) {
      if(terminal.selection.isDefined) {
        val sel = terminal.selection.get
        terminal.selection = None
        val shift = sel.begin + s.length - terminal.offset
        if (shift < 0) terminal.offset += shift
        document.replace(sel, s)
        if (shift > 0) terminal.offset += shift
      } else {
        document.insert(terminal.offset, s)
        terminal.offset += s.length
      }
    }
  }
}