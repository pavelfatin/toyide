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

import java.awt.{Graphics, Point, Rectangle}

import com.pavelfatin.toyide.Interval
import com.pavelfatin.toyide.editor.Area

private abstract class AbstractPainter(context: PainterContext) extends Painter {
  protected def document = context.document

  protected def terminal = context.terminal

  protected def data = context.data

  protected def canvas = context.canvas

  protected def grid = context.grid

  protected def coloring = context.coloring

  protected def contains(chars: CharSequence, char: Char): Boolean =
    Range(0, chars.length).exists(i => chars.charAt(i) == char)

  protected def fill(g: Graphics, r: Rectangle) {
    g.fillRect(r.x, r.y, r.width, r.height)
  }

  protected def toPoint(offset: Int): Point =
    grid.toPoint(document.toLocation(offset))

  protected def notifyObservers(interval: Interval) {
    rectanglesOf(interval).foreach(notifyObservers)
  }

  protected def lineRectangleAt(offset: Int): Rectangle = {
    val point = toPoint(offset)
    new Rectangle(0, point.y, canvas.size.width, grid.cellSize.height)
  }

  protected def rectanglesOf(interval: Interval): Seq[Rectangle] = {
    val width = canvas.size.width
    val height = grid.cellSize.height

    val p1 = toPoint(interval.begin)
    val p2 = toPoint(interval.end)

    if(p1.y == p2.y) {
      Seq(new Rectangle(p1.x, p1.y, p2.x - p1.x, height))
    } else {
      Seq(new Rectangle(p1.x, p1.y, width - p1.x, height),
        new Rectangle(grid.insets.left, p1.y + height, width - grid.insets.left, p2.y - p1.y - height),
        new Rectangle(grid.insets.left, p2.y, p2.x - grid.insets.left, height))
    }
  }

  protected def intervalOf(area: Area): Interval = {
    val beginLine = bound(area.line)
    val endLine = bound(area.line + area.height)
    Interval(document.startOffsetOf(beginLine), document.endOffsetOf(endLine))
  }

  private def bound(line: Int): Int = 0.max(line.min(document.linesCount - 1))
}
