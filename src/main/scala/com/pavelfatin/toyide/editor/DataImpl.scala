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

import com.pavelfatin.toyide.lexer._
import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.node.Node
import com.pavelfatin.toyide.inspection.{Mark, Inspection}
import com.pavelfatin.toyide.parser.Parser

private class DataImpl(document: Document, lexer: Lexer, parser: Parser, inspections: Seq[Inspection]) extends Data {
  private var state: State = new AnalyzerState()

  private trait State {
    def tokens = Seq[Token]()

    def tree: Option[Node] = None

    def marks = Seq[Mark]()

    lazy val errors: Seq[Error] = {
      val lexers = state.tokens.collect {
        case Token(_, span, Some(message)) => Error(span, message)
      }
      val parsers = state.tree.toSeq.flatMap(_.elements).map(node => (node, node.problem)).collect {
        case (node, Some(message)) => Error(node.span, message)
      }
      val inspected = state.marks.collect {
        case Mark(node, message, decoration, warning) => Error(node.span, message, decoration, !warning)
      }
      lexers ++ parsers ++ inspected
    }

    def hasNext = true

    def next: State = throw new IllegalStateException("Next state is unavailable")
  }

  private class AnalyzerState extends State {
    override lazy val tokens = lexer.analyze(document.characters).toSeq

    override def next = new ParserPass(tokens)
  }

  private class ParserPass(override val tokens: Seq[Token]) extends State {
    private lazy val root = parser.parse(tokens.toIterator)

    override lazy val tree = Some(root)

    override lazy val marks = root.elements.flatMap { node =>
      inspections.flatMap(_.inspect(node))
    }

    override def hasNext = false
  }

  document.onChange { event =>
    reset()
  }

  def reset() {
    state = new AnalyzerState()
    notifyObservers()
  }

  def hasNextPass = state.hasNext

  def nextPass() {
    state = state.next
    notifyObservers()
  }

  def compute() {
    while (hasNextPass)
      nextPass()
  }

  def tokens = state.tokens

  def structure = state.tree

  def errors = state.errors

  def hasFatalErrors = errors.exists(_.fatal)
}