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
import java.text.AttributedString

import com.pavelfatin.toyide.Interval
import com.pavelfatin.toyide.document.Location
import com.pavelfatin.toyide.editor._
import com.pavelfatin.toyide.editor.painter.TextPainter._
import com.pavelfatin.toyide.lexer.{Lexer, Token}

private class TextPainter(context: PainterContext, lexer: Lexer,
                          decorators: Seq[Decorator]) extends AbstractPainter(context) {

  def id = "text"

  private var string = EmptyString

  private var stringValid = true

  private var singleLineChanged = false

  document.onChange { event =>
    stringValid = false

    if (canvas.visible) {
      val replacement = event.asReplacement

      if (!contains(replacement.before, '\n') && !contains(replacement.after, '\n')) {
        singleLineChanged = true

        notifyObservers(lineRectangleAt(replacement.begin))
      } else {
        notifyObservers(canvas.visibleRectangle)
      }
    }
  }

  coloring.onChange {
    stringValid = false

    notifyObservers(canvas.visibleRectangle)
  }

  override def paint(g: Graphics, bounds: Rectangle) {
    val area = grid.toArea(bounds)

    if (singleLineChanged && area.height == 1) {
      paintLine(g, area)
    } else {
      if (!stringValid) {
        updateString()
      }
      paintArea(g, area)
    }

    singleLineChanged = false
  }

  private def updateString() {
    if (data.pass == Pass.Text) {
      data.nextPass()
    }
    string = render(document.text, data.tokens, coloring)
    stringValid = true
  }

  private def paintLine(g: Graphics, area: Area) {
    val rectangle = grid.toRectangle(area)

    val lineInterval = document.intervalOf(area.line)

    val lineText = document.text(lineInterval)

    if (lineText.length > 0) {
      val tokens = lexer.analyze(lineText).toSeq
      val string = render(lineText, tokens, coloring)

      val decorated = decorate(string, decorators, lineInterval, - lineInterval.begin)

      g.drawString(decorated.getIterator, rectangle.x, rectangle.y + 15)
    }
  }

  private def paintArea(g: Graphics, area: Area) {
    val decorated = decorate(string, decorators, intervalOf(area), 0)

    Range(area.line, (area.line + area.height).min(document.linesCount)).foreach { line =>
      val interval = {
        val lineInterval = document.intervalOf(line)
        val areaInterval = Interval(lineInterval.begin + area.indent, lineInterval.begin + area.indent + area.width)
        lineInterval.intersection(areaInterval)
      }

      if (!interval.empty) {
        val iterator = decorated.getIterator(null, interval.begin, interval.end)
        val p = grid.toPoint(Location(line, area.indent))
        g.drawString(iterator, p.x, p.y + 15)
      }
    }
  }
}

private object TextPainter {
  private val EmptyString = new AttributedString("")

  private def render(text: String, tokens: Seq[Token], coloring: Coloring): AttributedString = {
    val result = new AttributedString(text)

    if (!text.isEmpty) {
      result.addAttribute(TextAttribute.FAMILY, coloring.fontFamily)
      result.addAttribute(TextAttribute.SIZE, coloring.fontSize)

      tokens.foreach { token =>
        val attributes = coloring.attributesFor(token.kind)
        val span = token.span
        attributes.decorate(result, span.begin, span.end)
      }
    }

    result
  }

  private def decorate(string: AttributedString, decorators: Seq[Decorator], visible: Interval, shift: Int): AttributedString = {
    val decorations = decorators.flatMap(_.decorations.map(p =>
      (p._1.intersection(visible), p._2)).filterKeys(!_.empty)).toMap

    if (decorations.isEmpty) string else {
      val result = new AttributedString(string.getIterator)

      decorations.foreach { case (interval, attributes) =>
        attributes.foreach { case (key, value) =>
          result.addAttribute(key, value, interval.begin + shift, interval.end + shift)
        }
      }

      result
    }
  }
}
