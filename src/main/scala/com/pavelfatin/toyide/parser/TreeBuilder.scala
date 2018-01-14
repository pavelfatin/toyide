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

package com.pavelfatin.toyide.parser

import com.pavelfatin.toyide.lexer._
import com.pavelfatin.toyide.Span
import com.pavelfatin.toyide.node.NodeImpl

class TreeBuilder(input: Iterator[Token]) {
  private val in = input.filterNot(_.kind == Tokens.WS).buffered

  private var head: Option[Token] = None

  private var headSpan = Span("", 0, 0)

  private var regions = List(new MyRegion(headSpan))

  if(hasNext) {
    advance()
  }

  def ahead(kinds: TokenKind*) = {
    in.hasNext && kinds.contains(in.head.kind)
  }

  def matches(kinds: TokenKind*) = {
    head.exists(token => kinds.contains(token.kind))
  }

  def consume(kinds: TokenKind*) {
    if(matches(kinds: _*))
      consume()
    else
      error("Expected %s".format(kinds.map(_.name).mkString(", ")))
  }

  def consume() {
    val token = head.getOrElse(throw new NoSuchTokenException())
    if(regions.tail.isEmpty) throw new ConsumeWithoutRegionException()
    regions.head.add(NodeImpl.createLeaf(token))
    advance()
  }

  def grasp(kinds: TokenKind*): Boolean = {
    val matched = matches(kinds: _*)
    if(matched) consume()
    matched
  }

  def error(message: String) {
    val region = regions.head

    if(region.hasProblem) return

    if(isEOF) {
      val span = Span(headSpan.source, headSpan.end, headSpan.end)
      region.add(NodeImpl.createError(None, span, message))
    } else {
      val token = head.getOrElse(throw new NoSuchTokenException())
      region.add(NodeImpl.createError(Some(token), token.span, message))
//      advance()
    }
  }

  def isEOF = !hasNext && head.isEmpty

  private def hasNext = in.hasNext

  def advance() {
    head = if(hasNext) Some(in.next()) else None
    head.foreach { token =>
      headSpan = token.span
    }
  }

  def tree: NodeImpl = {
    if(!regions.tail.isEmpty) throw new UnclosedRegionException
    val nodes = regions.head.nodes
    val root = nodes.headOption.getOrElse(throw new NoRootNodeException)
    if(!nodes.tail.isEmpty) throw new MultipleRootNodesException()
    root
  }

  def open(): Region = {
    val region = new MyRegion(headSpan)
    regions ::= region
    region
  }

  def capturing(node: NodeImpl, collapseHolderNode: Boolean = false)(action: => Unit) {
    val region = open()
    action
    region.close(node, collapseHolderNode)
  }

  def folding(node: => NodeImpl, collapseHolderNode: Boolean = false, length: Int = 3)(action: => Unit) {
    val region = open()
    action
    region.fold(node, collapseHolderNode, length)
  }

  private class MyRegion(tokenSpan: Span) extends Region {
    private var entries: List[NodeImpl] = Nil
    private var closed = false

    def close(node: NodeImpl, collapseHolderNode: Boolean) {
      capture(node, collapseHolderNode)(children => children)
    }

    def fold(node: => NodeImpl, collapseHolderNode: Boolean, length: Int) {
      capture(node, collapseHolderNode) {
        case Nil => Nil
        case head :: Nil => Seq(head)
        case head :: tail => {
          val root = tail.grouped(length - 1).foldLeft(head) { (left, part) =>
            val parent = node // call-by-name for factory method
            parent.children = Seq(left) ++ part
            parent
          }
          root.children
        }
      }
    }

    private def capture(node: NodeImpl, collapseHolderNode: Boolean = false)(f: Seq[NodeImpl] => Seq[NodeImpl]) {
      if(closed) throw new MultipleClosingException
      if(!this.eq(regions.head)) throw new IncorrectRegionsOrderException
      regions = regions.tail
      val children = if(collapseHolderNode && nodes.size == 1) nodes.head else {
        if(entries.isEmpty)
          node.span = tokenSpan.leftEdge
        else
          node.children = f(nodes)
        node
      }
      regions.head.add(children)
      closed = true
    }

    def add(node: NodeImpl) {
      if(closed) throw new RuntimeException("Unable to add node to closed region")
      entries ::= node
    }

    def hasProblem = entries.exists(_.elements.exists(_.problem.isDefined))

    def nodes = entries.reverse
  }
}

trait Region {
  def close(node: NodeImpl, collapseHolderNode: Boolean = false)

  def fold(node: => NodeImpl, collapseHolderNode: Boolean = false, length: Int = 3)
}

class NoRootNodeException extends RuntimeException

class MultipleRootNodesException extends RuntimeException

class NoSuchTokenException extends RuntimeException

class ConsumeWithoutRegionException extends RuntimeException

class UnclosedRegionException extends RuntimeException

class MultipleClosingException extends RuntimeException

class IncorrectRegionsOrderException extends RuntimeException