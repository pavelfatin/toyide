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
