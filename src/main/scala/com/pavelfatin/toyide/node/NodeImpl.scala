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