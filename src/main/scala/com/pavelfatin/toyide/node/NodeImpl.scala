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

package com.pavelfatin.toyide.node

import com.pavelfatin.toyide.lexer.Token
import com.pavelfatin.toyide.Span

class NodeImpl(val kind: String) extends Node {
  var token: Option[Token] = None

  var span: Span = Span("", 0, 0)

  var problem: Option[String] = None

  var parent: Option[Node] = None

  var previousSibling: Option[Node] = None

  var nextSibling: Option[Node] = None

  private var _children: Seq[NodeImpl] = Seq.empty

  def children = _children

  def children_=(children: Seq[NodeImpl]) {
    val first = children.head.span
    span = Span(first.source, first.begin, children.last.span.end)
    _children = children
    children.foreach(_.parent = Some(this))
    for((a, b) <- children.zip(children.tail)) {
      a.nextSibling = Some(b)
      b.previousSibling = Some(a)
    }
  }
}

object NodeImpl {
  def createLeaf(token: Token) = {
    val node = new NodeImpl("leaf")
    node.token = Some(token)
    node.span = token.span
    node
  }

  def createError(token: Option[Token], span: Span, message: String) = {
    val node = new NodeImpl("leaf")
    node.span = span
    node.token = token
    node.problem = Some(message)
    node
  }
}