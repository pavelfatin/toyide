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

import com.pavelfatin.toyide.node.{IdentifiedNode, ReferenceNodeTarget, Node}
import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.Interval

package object controller {
  private[controller] implicit class DataExt(val data: Data) extends AnyVal {
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
          case ref @ ReferenceNodeTarget(target) if targetNode.contains(target) => ref
        }
      }
      targetNode.flatMap(_.id).toList ::: refs.flatMap(_.source)
    }
  }

  private[controller] implicit class NodeExt(val node: Node) extends AnyVal {
    def offsetOf(i: Int): Option[Int] = {
      if (node.span.touches(i)) Some(i - node.span.begin) else None
    }
  }

  private[controller] implicit class TerminalExt(val terminal: Terminal) extends AnyVal {
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