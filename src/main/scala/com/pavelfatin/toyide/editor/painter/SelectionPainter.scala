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
import java.awt.{Graphics, Rectangle}

import com.pavelfatin.toyide.editor.{Coloring, SelectionChange}

private class SelectionPainter(context: PainterContext) extends AbstractPainter(context) with Decorator {
  def id = "selection"

  terminal.onChange {
    case SelectionChange(from, to) =>
      from.foreach(notifyObservers)
      to.foreach(notifyObservers)
    case _ =>
  }

  override def paint(g: Graphics, bounds: Rectangle) {
    val rectangles = terminal.selection.toSeq.flatMap(rectanglesOf)
      .map(_.intersection(bounds)).filterNot(_.isEmpty)

    if (rectangles.nonEmpty) {
      g.setColor(coloring(Coloring.SelectionBackground))
      rectangles.foreach(fill(g, _))
    }
  }

  override def decorations = terminal.selection
    .map(interval => (interval, Map(TextAttribute.FOREGROUND -> coloring(Coloring.SelectionForeground)))).toMap
}
