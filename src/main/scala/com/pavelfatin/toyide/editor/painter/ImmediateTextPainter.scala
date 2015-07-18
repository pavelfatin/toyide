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
import java.text.AttributedString

import com.pavelfatin.toyide.document.{DocumentEvent, Insertion, Removal}
import com.pavelfatin.toyide.editor.{Adviser, Area, Coloring}
import com.pavelfatin.toyide.lexer.Lexer

private class ImmediateTextPainter(context: PainterContext, lexer: Lexer) extends AbstractPainter(context) {
  def id = "immediate text"

  override def immediate = true

  private var lastEvent: Option[DocumentEvent] = None

  document.onChange {
    case event @ Insertion(offset, chars) if isRelevantEvent(offset, chars) =>
      lastEvent = Some(event)
      notifyObservers(rectangleFrom(offset, tailLengthFrom(offset)))

    case event @ Removal(begin, _, chars) if isRelevantEvent(begin, chars) =>
      lastEvent = Some(event)
      notifyObservers(rectangleFrom(begin, tailLengthFrom(begin) + chars.length))

    case _ =>
  }

  private def isRelevantEvent(offset: Int, chars: CharSequence) =
    canvas.visible && !contains(chars, '\n') && chars != Adviser.Anchor

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
    lastEvent match {
      case Some(Insertion(offset, chars)) => paintInsertion(g, offset, chars)
      case Some(Removal(begin, _, chars)) => paintRemoval(g, begin, chars)
      case _ =>
    }
    lastEvent = None
  }

  private def paintInsertion(g: Graphics, offset: Int, chars: CharSequence) {
    val tailLength = tailLengthFrom(offset + chars.length)

    val remainder = rectangleFrom(offset, tailLength)

    if (!remainder.isEmpty) {
      val delta = grid.cellSize.width * chars.length
      g.copyArea(remainder.x, remainder.y, remainder.width, remainder.height, delta, 0)
    }

    val location = document.toLocation(offset)

    val prefix = document.text(document.startOffsetOf(location.line), offset + chars.length)

    lexer.analyze(prefix).toSeq.lastOption.foreach { token =>
      val area = Area(location.line, location.indent, chars.length, 1)
      val rectangle = grid.toRectangle(area)

      g.setColor(backgroundColorAt(offset))
      fill(g, rectangle)

      val string = new AttributedString(chars.toString)
      string.addAttribute(TextAttribute.FAMILY, coloring.fontFamily)
      string.addAttribute(TextAttribute.SIZE, coloring.fontSize)

      val attributes = coloring.attributesFor(token.kind)
      attributes.decorate(string, 0, chars.length)

      g.drawString(string.getIterator, rectangle.x, rectangle.y + 15)
    }
  }

  private def paintRemoval(g: Graphics, begin: Int, chars: CharSequence) {
    val tailLength = tailLengthFrom(begin)

    val remainder = rectangleFrom(begin, tailLength)

    val delta = grid.cellSize.width * chars.length

    if (!remainder.isEmpty) {
      g.copyArea(remainder.x + delta, remainder.y, remainder.width, remainder.height, - delta, 0)
    }

    val rectangle = new Rectangle(remainder.x + remainder.width, remainder.y, delta, remainder.height)

    g.setColor(backgroundColorAt(begin))
    fill(g, rectangle)
  }

  private def backgroundColorAt(offset: Int): Color = {
    val currentLine = document.lineNumberOf(offset) == document.lineNumberOf(terminal.offset)
    if (currentLine) coloring(Coloring.CurrentLineBackground) else coloring(Coloring.TextBackground)
  }
}