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

package com.pavelfatin.toyide.formatter

import com.pavelfatin.toyide.node.Node
import com.pavelfatin.toyide.lexer.Token
import com.pavelfatin.toyide.Interval
import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.formatter.Distance._


class FormatterImpl(format: Format) extends Formatter {
  def format(root: Node, selection: Option[Interval], tabSize: Int): String = {
    val (tokens, interval) = affectedTokens(root, selection)
    if(tokens.isEmpty) root.span.text else {
      var column = 0
      val parts = tokens.zip(tokens.tail).map { p =>
        column += format.indentDeltaFor(p._1.kind, p._2.kind)
        format(p._1, p._2, column * tabSize)
      }
      val formatted = (parts ++ tokens.map(_.span.text).lastOption.toSeq).mkString
      val prefix = root.span.source.subSequence(0, interval.begin)
      val suffix = root.span.source.subSequence(interval.end)
      prefix + formatted + suffix
    }
  }

  private def format(a: Token, b: Token, indent: Int): String = {
    val actual = distanceBetween(a, b)
    val expected = format.distanceFor(a.kind, b.kind)
    a.span.text + format(actual, expected, indent)
  }

  private def distanceBetween(a: Token, b: Token): Distance = {
    val s = a.span.source.subSequence(a.span.end, b.span.begin)
    val lines = s.count(_ == '\n')
    if(lines > 0) Lines(lines) else Spaces(s.count(_ == ' '))
  }

  private def format(actual: Distance, expected: Distance, indent: Int) = {
    val prefix = Seq.fill(indent)(" ").mkString
    expected match {
      case Joint => ""
      case Space => " "
      case Lines => actual match {
        case lines: Lines => Seq.fill(lines.n)("\n").mkString + prefix
        case _ => "\n"  + prefix
      }
      case LinesOrSpace => actual match {
        case _: Spaces => " "
        case lines: Lines => Seq.fill(lines.n)("\n").mkString  + prefix
      }
    }
  }

  private def affectedTokens(root: Node, selection: Option[Interval]): (Seq[Token], Interval) = {
    val interval = selection.getOrElse(root.span.interval)

    val selected = root.elements.flatMap(_.token.toSeq).distinct.filter(_.span.interval.intersectsWith(interval))

    val begin = selected.headOption.map(_.span.begin).getOrElse(root.span.begin)
    val end = selected.lastOption.map(_.span.end).getOrElse(root.span.end)

    (selected, Interval(begin, end))
  }
}