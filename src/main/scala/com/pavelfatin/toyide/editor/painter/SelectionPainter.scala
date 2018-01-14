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
