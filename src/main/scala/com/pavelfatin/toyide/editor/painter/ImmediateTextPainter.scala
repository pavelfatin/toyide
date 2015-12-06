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

package com.pavelfatin.toyide.editor.painter

import java.awt.font.TextAttribute
import java.awt.{Color, Graphics, Rectangle}
import java.lang.Math._
import java.text.AttributedString

import com.pavelfatin.toyide.document.Replacement
import com.pavelfatin.toyide.editor.{Adviser, Area, Coloring}
import com.pavelfatin.toyide.lexer.Lexer

private class ImmediateTextPainter(context: PainterContext, lexer: Lexer) extends AbstractPainter(context) {
  private val Pairs = Set("()", "{}", "\"\"")

  def id = "immediate text"

  override def immediate = true

  private var lastEvent: Option[Replacement] = None

  document.onChange { event =>
    if (canvas.visible) {
      val replacement = event.asReplacement

      if (isRelevant(replacement)) {
        lastEvent = Some(replacement)

        val lengthBefore = replacement.before.length
        val lengthAfter = replacement.after.length
        val endAfter = replacement.begin + lengthAfter
        val rectangle = rectangleFrom(replacement.begin, max(lengthBefore, lengthAfter) + tailLengthFrom(endAfter) + 1)
        notifyObservers(rectangle)
      }
    }
  }

  private def isRelevant(replacement: Replacement): Boolean =
    !contains(replacement.before, '\n') && !contains(replacement.after, '\n') &&
      !(replacement.after.length == 2 && Pairs.contains(replacement.after.toString)) &&
        replacement.after != Adviser.Anchor

  private def tailLengthFrom(offset: Int): Int = {
    val location = document.toLocation(offset)
    document.endOffsetOf(location.line) - offset
  }

  private def rectangleFrom(offset: Int, length: Int): Rectangle = {
    val location = document.toLocation(offset)
    val area = Area(location.line, location.indent, length, 1)
    grid.toRectangle(area)
  }

  override def paint(g: Graphics, bounds: Rectangle) {
    lastEvent.foreach {
      case Replacement(begin, _, before, after) =>
        paintReplacement(g, begin, before, after)
    }
    lastEvent = None
  }

  private def paintReplacement(g: Graphics, begin: Int, before: CharSequence, after: CharSequence) {
    val endAfter = begin + after.length
    val delta = after.length - before.length

    if (delta != 0) {
      val tailLength = tailLengthFrom(endAfter)

      if (tailLength > 0) {
        val shift = grid.cellSize.width * delta
        val tailAfter = rectangleFrom(endAfter, tailLength)

        g.copyArea(tailAfter.x - shift, tailAfter.y, tailAfter.width, tailAfter.height, shift, 0)
      }

      if (delta < 0) {
        val exposedBackground = rectangleFrom(endAfter + tailLength, -delta + 1)

        g.setColor(backgroundColorAt(begin))
        fill(g, exposedBackground)
      }
    }

    if (after.length > 0) {
      val location = document.toLocation(begin)

      val prefix = document.text(document.startOffsetOf(location.line), endAfter)

      lexer.analyze(prefix).toSeq.lastOption.foreach { token =>
        val area = Area(location.line, location.indent, after.length, 1)
        val rectangle = grid.toRectangle(area)

        g.setColor(backgroundColorAt(begin))
        fill(g, rectangle)

        val string = new AttributedString(after.toString)
        string.addAttribute(TextAttribute.FAMILY, coloring.fontFamily)
        string.addAttribute(TextAttribute.SIZE, coloring.fontSize)

        val attributes = coloring.attributesFor(token.kind)
        attributes.decorate(string, 0, after.length)

        g.drawString(string.getIterator, rectangle.x, rectangle.y + 15)
      }
    }

    g.setColor(coloring(Coloring.CaretForeground))
    fill(g, caretRectangleAt(terminal.offset + max(0, delta)))
  }

  private def backgroundColorAt(offset: Int): Color = {
    val currentLine = document.lineNumberOf(offset) == document.lineNumberOf(terminal.offset)
    if (currentLine) coloring(Coloring.CurrentLineBackground) else coloring(Coloring.TextBackground)
  }
}