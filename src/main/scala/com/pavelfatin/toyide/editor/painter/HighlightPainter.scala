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

import java.awt.{Graphics, Rectangle}

import com.pavelfatin.toyide.editor.{Coloring, HighlightsChange}

private class HighlightPainter(context: PainterContext) extends AbstractPainter(context) {
  terminal.onChange {
    case HighlightsChange(from, to) =>
      from.foreach(notifyObservers)
      to.foreach(notifyObservers)
    case _ =>
  }

  def id = "highlight"

  def paint(g: Graphics, bounds: Rectangle) {
    val rectangles = terminal.highlights.flatMap(rectanglesOf)
      .map(_.intersection(bounds)).filterNot(_.isEmpty)

    if (rectangles.nonEmpty) {
      g.setColor(coloring(Coloring.HighlightBackground))
      rectangles.foreach(fill(g, _))
    }
  }
}
