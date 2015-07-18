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

import com.pavelfatin.toyide.Interval
import com.pavelfatin.toyide.editor._
import com.pavelfatin.toyide.node.ReferenceNode

private class HoverPainter(context: PainterContext) extends AbstractPainter(context) with Decorator {
  private val HoverAttributes = Map(
    TextAttribute.FOREGROUND -> Color.BLUE,
    TextAttribute.UNDERLINE -> TextAttribute.UNDERLINE_ON)

  terminal.onChange {
    case HoverChange(from, to) =>
      from.foreach(offset => hoverInterval(offset).foreach(notifyObservers))
      to.foreach(offset => hoverInterval(offset).foreach(notifyObservers))
    case _ =>
  }

  private def hoverInterval(offset: Int): Option[Interval] = {
    data.structure.flatMap(_.elements.find(node =>
      node.isInstanceOf[ReferenceNode] && node.span.includes(offset))).map(_.span.interval)
  }

  def id = "hover"

  def paint(g: Graphics, bounds: Rectangle) {}

  override def decorations = terminal.hover.flatMap(hoverInterval)
    .map(interval => (interval, HoverAttributes)).toMap
}
