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

package com.pavelfatin.toyide

import document.{DocumentImpl, Document}
import editor.{TerminalMock, Terminal}
import org.junit.Assert._
import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.node.{ReferenceNode, Node}

object Helpers {
  def assertMatches[T](actual: T)(pattern: PartialFunction[T, Unit]) {
    assertTrue("actual: " + actual.toString, pattern.isDefinedAt(actual))
  }

  def assertNoProblemsIn(elements: Seq[Node]) {
    assertEquals(List.empty, elements.flatMap(_.token).filter(_.problem.isDefined).toList)
    assertEquals(List.empty, elements.filter(_.problem.isDefined).toList)
  }

  def assertNoUnresolvedIn(elements: Seq[Node]) {
    assertEquals(List.empty, elements.filterBy[ReferenceNode].filter(_.unresolved))
  }

  object Target {
    def unapply(node: Node) = {
      Some((node.span.text, node.span.begin))
    }
  }

  object Text {
    def unapply(node: Node) = {
      Some(node.span.text)
    }
  }

  object Offset {
    def unapply(node: Node) = {
      Some(node.span.begin)
    }
  }

  object Line {
    def unapply(node: Node) = {
      node.span.source.take(node.span.begin).count(_ == '\n')
    }
  }

  def parseDocument(code: String): (Document, Terminal) = {
    val offset = code.diff(Seq('[', ']')).indexOf('|')

    if(offset < 0) throw new IllegalArgumentException("Cursor position isn't specified: %s".format(code))

    val s = code.diff(Seq('|'))

    val selection = (s.indexOf('['), s.lastIndexOf(']')) match {
      case (a, b) if a >= 0 && b > a => Some(Interval(a, b - 1))
      case (a, b) if a < 0 && b >= 0 =>
        throw new IllegalArgumentException("Selection start isn't specified: %s".format(code))
      case (a, b) if a >= 0 && b < 0 =>
        throw new IllegalArgumentException("Selection end isn't specified: %s".format(code))
      case _ => None
    }

    val cleanCode = code.diff(Seq('[', ']', '|'))

    val document = new DocumentImpl(cleanCode)
    val terminal = new TerminalMock(offset, selection)

    document.onChange { event =>
      assertInSync(document, terminal)
    }
    terminal.onChange { event =>
      assertInSync(document, terminal)
    }

    (document, terminal)
  }

  private def assertInSync(document: Document, terminal: Terminal) {
    val interval = Interval(0, document.length)
    assertWithin(interval, terminal.offset)
    terminal.selection.foreach(assertWithin(interval, _))
    terminal.hover.foreach(assertWithin(interval, _))
    terminal.highlights.foreach(assertWithin(interval, _))
  }

  private def assertWithin(interval: Interval, it: Interval) {
    assertWithin(interval, it.begin)
    assertWithin(interval, it.end)
    assertTrue(it.begin <= it.end)
  }

  private def assertWithin(interval: Interval, i: Int) {
    assertTrue("%d must be within %s".format(i, interval.toString), interval.touches(i))
  }

  def formatDocument(document: Document, view: Terminal): String = {
    val selection = view.selection.toSeq
    val insertions = selection.map(_.end -> ']') ++ Seq(view.offset -> '|') ++ selection.map(_.begin -> '[')
    val builder = new StringBuilder(document.text)
    for((i, c) <- insertions.sortBy(-_._1)) builder.insert(i, c)
    builder.toString()
  }
}