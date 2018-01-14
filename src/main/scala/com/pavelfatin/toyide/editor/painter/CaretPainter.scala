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

private class CaretPainter(context: PainterContext) extends AbstractPainter(context) {
  def id = "caret"

  terminal.onChange {
    case CaretMovement(from, to) =>
      notifyObservers(caretRectangleAt(from))
      notifyObservers(caretRectangleAt(to))
    case _ =>
  }

  canvas.onChange {
    case CaretVisibilityChanged(_) =>
      notifyObservers(caretRectangleAt(terminal.offset))
    case _ =>
  }

  override def paint(g: Graphics, bounds: Rectangle) {
    if (canvas.caretVisible) {
      val rectangle = caretRectangleAt(terminal.offset).intersection(bounds)

      if (!rectangle.isEmpty) {
        g.setColor(coloring(Coloring.CaretForeground))
        fill(g, rectangle)
      }
    }
  }
}
