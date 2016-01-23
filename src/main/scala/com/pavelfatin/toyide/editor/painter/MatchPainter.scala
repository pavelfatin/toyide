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

import com.pavelfatin.toyide.Interval
import com.pavelfatin.toyide.document.Bias
import com.pavelfatin.toyide.editor._
import com.pavelfatin.toyide.lexer.Token

private class MatchPainter(context: PainterContext, matcher: BraceMatcher,
                           processor: ActionProcessor) extends AbstractPainter(context) {

  private var anchoredMatches = Seq.empty[AnchoredMatch]

  private var completeData = true

  new Delay(terminal, processor).onChange {
    case CaretMovement(_, _) if data.pass != Pass.Text => update()
    case SelectionChange(Some(_), None) if data.pass != Pass.Text => update()
    case _ =>
  }

  data.onChange {
    case DataEvent(Pass.Lexer, _) => update()
    case _ =>
  }
  
  canvas.onChange {
    case VisibleRectangleChanged(_) if !completeData && anchoredMatches.nonEmpty => update(complete = true)
    case FocusChanged(hasFocus) => update(complete = true)
    case _ =>
  }

  private def update(complete: Boolean = false) {
    anchoredMatches.foreach(_.dispose())

    val previousMatches = anchoredMatches

    anchoredMatches = if (!canvas.hasFocus || terminal.selection.isDefined) Seq.empty else {
      val tokens = if (complete) data.tokens else {
        val visibleInterval = intervalOf(grid.toArea(canvas.visibleRectangle))
        data.tokens.filter(_.span.intersectsWith(visibleInterval))
      }
      matchIntervalsIn(tokens, terminal.offset).map(p => new AnchoredMatch(p._1.interval, p._2)).toVector
    }

    previousMatches.foreach(it => notifyObservers(it.interval))
    anchoredMatches.foreach(it => notifyObservers(it.interval))

    completeData = complete
  }

  private def matchIntervalsIn(tokens: Seq[Token], offset: Int) = tokens.flatMap { token =>
    matcher.braceTypeOf(token, data.tokens, offset) match {
      case Paired => Seq((token.span, Paired))
      case Unbalanced => Seq((token.span, Unbalanced))
      case Inapplicable => Seq.empty
    }
  }

  def id = "match"

  def paint(g: Graphics, bounds: Rectangle) {
    anchoredMatches.foreach { it =>
      val rectangle = toRectangle(it.interval).intersection(bounds)

      if (!rectangle.isEmpty) {
        val color = colorFor(it.braceType)
        g.setColor(color)
        fill(g, rectangle)
      }
    }
  }

  private def colorFor(braceType: BraceType) = braceType match {
    case Paired => coloring(Coloring.PairedBraceBackground)
    case Unbalanced => coloring(Coloring.UnbalancedBraceBackground)
    case Inapplicable => coloring(Coloring.TextBackground)
  }

  private def toRectangle(interval: Interval): Rectangle = {
    val location = document.toLocation(interval.begin)
    val area = Area(location.line, location.indent, interval.length, 1)
    grid.toRectangle(area)
  }

  private class AnchoredMatch(origin: Interval, val braceType: BraceType) {
    private val beginAnchor = document.createAnchorAt(origin.begin, Bias.Right)

    private val endAnchor = document.createAnchorAt(origin.end, Bias.Left)

    def interval = Interval(beginAnchor.offset, beginAnchor.offset.max(endAnchor.offset))

    def dispose() {
      beginAnchor.dispose()
      endAnchor.dispose()
    }
  }
}
