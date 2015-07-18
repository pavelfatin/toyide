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

import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.inspection.{Inspection, Mark}
import com.pavelfatin.toyide.lexer._
import com.pavelfatin.toyide.node.Node
import com.pavelfatin.toyide.parser.Parser

private class DataImpl(document: Document, lexer: Lexer, parser: Parser, inspections: Seq[Inspection]) extends Data {
  def text = document.text

  var tokens = Seq.empty[Token]

  var structure: Option[Node] = None

  var errors = Seq.empty[Error]

  var hasFatalErrors = errors.exists(_.fatal)

  var pass: Pass = Pass.Text

  document.onChange { _ =>
    run(Pass.Text)
  }

  def hasNextPass = pass.next.isDefined

  def nextPass() {
    val next = pass.next.getOrElse(
      throw new IllegalStateException("Next pass is unavailable"))

    run(next)
  }

  def compute() {
    while (hasNextPass) {
      nextPass()
    }
  }

  private def run(p: Pass) {
    pass = p

    val passErrors = p match {
      case Pass.Text => runTextPass()
      case Pass.Lexer => runLexerPass()
      case Pass.Parser => runParserPass()
      case Pass.Inspections => runInspectionPass()
    }

    errors = errors ++ passErrors
    hasFatalErrors = hasFatalErrors || passErrors.exists(_.fatal)

    notifyObservers(DataEvent(pass, passErrors))
  }

  private def runTextPass(): Seq[Error] = {
    tokens = Seq.empty
    structure = None
    errors = Seq.empty
    hasFatalErrors = false

    Seq.empty
  }

  private def runLexerPass(): Seq[Error] = {
    tokens = lexer.analyze(document.characters).toSeq

    tokens.collect {
      case Token(_, span, Some(message)) => Error(span.interval, message)
    }
  }

  private def runParserPass(): Seq[Error] = {
    val root = parser.parse(tokens.toIterator)

    structure = Some(root)

    root.elements.map(node => (node, node.problem)).collect {
      case (node, Some(message)) => Error(node.span.interval, message)
    }
  }

  private def runInspectionPass(): Seq[Error] = {
    val root = structure.getOrElse(
      throw new IllegalStateException("Running inspections prior to parser"))

    root.elements.flatMap(node => inspections.flatMap(_.inspect(node))).collect {
      case Mark(node, message, decoration, warning) => Error(node.span.interval, message, decoration, !warning)
    }
  }
}
