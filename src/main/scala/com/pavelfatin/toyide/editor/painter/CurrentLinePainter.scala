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

import java.awt._

import com.pavelfatin.toyide.editor._

private class CurrentLinePainter(context: PainterContext) extends AbstractPainter(context) {
  def id = "current line"

  terminal.onChange {
    case CaretMovement(from, to) =>
      val fromRectangle = lineRectangleAt(from)
      val toRectangle = lineRectangleAt(to)

      if (fromRectangle != toRectangle) {
        notifyObservers(fromRectangle)
        notifyObservers(toRectangle)
      }
    case _ =>
  }

  override def paint(g: Graphics, bounds: Rectangle) {
    val rectangle = lineRectangleAt(terminal.offset).intersection(bounds)

    if (!rectangle.isEmpty) {
      g.setColor(coloring(Coloring.CurrentLineBackground))
      fill(g, rectangle)
    }
  }
}
