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

package com.pavelfatin.toyide.editor

import com.pavelfatin.toyide.inspection.Decoration
import com.pavelfatin.toyide.node.ReferenceNode
import java.awt.Color
import com.pavelfatin.toyide.lexer.Token
import com.pavelfatin.toyide.Interval
import com.pavelfatin.toyide.document.Location

private class RendererImpl(coloring: Coloring, matcher: BraceMatcher) extends Renderer {
  def render(data: Data, terminal: Terminal, begin: Int, end: Int): Seq[Text] = {
    var line = 0
    var indent = 0

    val reds = data.errors.filter(_.decoration == Decoration.Red)
    val dims = data.errors.filter(_.decoration == Decoration.Dim)

    data.tokens.flatMap { token =>
      val span = token.span
      val s = span.text
      if(s.contains("\n")) {
        line += s.count(_ == '\n')
        indent = s.view.reverse.takeWhile(_ != '\n').size
        Seq.empty
      } else if (line < begin || line >= end) {
        Seq.empty
      } else {
        var attributes = coloring.attributesFor(token.kind)
        if (terminal.selection.isEmpty) {
          for(i <- terminal.hover if span.touches(i);
              root <- data.structure;
              node <- root.elements;
              nodeToken <- node.token if nodeToken.eq(token);
              parent <- node.parent if parent.isInstanceOf[ReferenceNode]) {
            attributes = attributes.copy(underlined = true, color = Color.BLUE)
          }
          matcher.braceTypeOf(token, data.tokens, terminal.offset) match {
            case Inapplicable => // do nothing
            case Paired => attributes = attributes.copy(background = Some(new Color(153, 204, 255)))
            case Unbalanced => attributes = attributes.copy(background = Some(new Color(255, 220, 220)))
          }
        }
        val texts = terminal.selection.map(createText(token, line, indent, attributes, _))
          .getOrElse(Seq(Text(Location(line, indent), s, attributes)))
        indent += s.length
        // TODO it's better to identify by node somehow, not be span (wipWith root.elements?)
        if(reds.exists(_.span.includes(span)))
          texts.map(text => text.copy(attributes = coloring.highlight(text.attributes)))
        else if(dims.exists(_.span.includes(span)))
          texts.map(text => text.copy(attributes = coloring.dim(text.attributes)))
        else
          texts
      }
    }
  }

  private def createText(token: Token, line: Int, indent: Int, attributes: Attributes, it: Interval): Seq[Text] = {
    val span = token.span

    def splitAt(i: Int) = span.end.min(span.begin.max(i)) - span.begin

    val a = splitAt(it.begin)
    val b = splitAt(it.end)

    val s = span.text

    val texts = Seq(Text(Location(line, indent), s.substring(0, a), attributes),
      Text(Location(line, indent + a), s.substring(a, b), coloring.invert(attributes)),
      Text(Location(line, indent + b), s.substring(b), attributes))

    texts.filter(_.s.nonEmpty)
  }
}