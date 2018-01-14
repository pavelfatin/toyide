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
