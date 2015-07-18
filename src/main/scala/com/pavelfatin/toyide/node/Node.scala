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
import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.interpreter.Evaluable
import com.pavelfatin.toyide.compiler.Translatable
import com.pavelfatin.toyide.optimizer.Optimizable

trait Node extends Evaluable with Translatable with Optimizable {
  def kind: String

  def token: Option[Token]

  def span: Span

  def problem: Option[String]

  def children: Seq[Node]

  def parent: Option[Node]

  def previousSibling: Option[Node]

  def nextSibling: Option[Node]

  def parents: Seq[Node] = {
    def of(node: Node): Stream[Node] = {
      node.parent match {
        case Some(parent) => parent #:: of(parent)
        case None => Stream.empty
      }
    }
    of(this)
  }

//  def root: Node = parents.lastOption.getOrElse(this)

  def previousSiblings: Seq[Node] = {
    def of(node: Node): Stream[Node] = {
      node.previousSibling match {
        case Some(sibling) => sibling #:: of(sibling)
        case None => Stream.empty
      }
    }
    of(this)
  }

  def nextSiblings: Seq[Node] = {
    def of(node: Node): Stream[Node] = {
      node.nextSibling match {
        case Some(sibling) => sibling #:: of(sibling)
        case None => Stream.empty
      }
    }
    of(this)
  }

  def isLeaf = token.isDefined

  def elements: Seq[Node] = {
    def elements(node: Node): Stream[Node] =
      node #:: node.children.toStream.flatMap(elements)
    elements(this)
  }

  def leafAt(offset: Int): Option[Node] = {
    if (offset < 0 || offset > span.end)
      throw new IllegalArgumentException("Offset (%d) must be in (%d; %d)".format(offset, span.begin, span.end))
    elements.filter(_.span.touches(span.begin + offset)).find(_.isLeaf)
  }

  def referenceAt(offset: Int): Option[ReferenceNode] = {
    if (offset < 0 || offset > span.end)
      throw new IllegalArgumentException("Offset (%d) must be in (%d; %d)".format(offset, span.begin, span.end))
    elements.filter(_.span.touches(span.begin + offset)).findBy[ReferenceNode]
  }

  def identifierAt(offset: Int): Option[IdentifiedNode] = {
    if (offset < 0 || offset > span.end)
      throw new IllegalArgumentException("Offset (%d) must be in (%d; %d)".format(offset, span.begin, span.end))
    val candidates = elements.filter(_.span.touches(span.begin + offset)).collect {
      case node @ NodeParent(identified: IdentifiedNode) if identified.id.contains(node) => identified
    }
    candidates.headOption
  }

  def content: String = {
    def indent(s: String, level: Int) =
      s.split("\n").map(Array.fill(level)("  ").mkString + _).mkString("\n")

    val prefix = if(problem.isDefined) "error: " else ""

    if(isLeaf)
      prefix + token.get.toString
    else
      prefix + kind + "\n" + children.map(n => indent(n.content, 1)).mkString("\n")
  }

  override def toString = {
    val prefix = if(problem.isDefined) "error: " else ""

    if(isLeaf)
      prefix + token.get.toString
    else
      prefix + kind
  }
}

object NodeToken {
  def unapply(node: Node) = node.token
}

object NodeParent {
  def unapply(node: Node) = node.parent
}

object NodeNextSibling {
  def unapply(node: Node) = node.nextSibling
}
