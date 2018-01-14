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

  protected def caretRectangleAt(offset: Int): Rectangle = {
    val point = toPoint(offset)
    new Rectangle(point.x, point.y, 2, grid.cellSize.height)
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
