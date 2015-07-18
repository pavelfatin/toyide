/*
 * Copyright (C) 2014 Pavel Fatin <http://pavelfatin.com>
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

package com.pavelfatin.toyide.languages.lisp

import java.awt.Color

import com.pavelfatin.toyide.editor.{Attributes, _}
import com.pavelfatin.toyide.languages.lisp.LispTokens._
import com.pavelfatin.toyide.lexer.TokenKind

class LispColoring(colors: Map[String, Color]) extends AbstractColoring(colors) {
  def attributesFor(kind: TokenKind) = {
    val foreground = apply(colorId(kind))
    val weight = weightFor(kind)
    val style = styleFor(kind)
    Attributes(foreground, None, weight, style, underlined = false)
  }

  private def colorId(kind: TokenKind) = kind match {
    case COMMENT => Coloring.Comment
    case BOOLEAN_LITERAL => Coloring.BooleanLiteral
    case INTEGER_LITERAL => Coloring.IntegerLiteral
    case CHARACTER_LITERAL => Coloring.CharLiteral
    case STRING_LITERAL => Coloring.StringLiteral
    case PREDEFINED_SYMBOL => Coloring.Keyword
    case _ => Coloring.TextForeground
  }

  private def weightFor(token: TokenKind) = token match {
    case BOOLEAN_LITERAL => Weight.Bold
    case STRING_LITERAL => Weight.Bold
    case PREDEFINED_SYMBOL => Weight.Bold
    case _ => Weight.Normal
  }

  private def styleFor(token: TokenKind) = token match {
    case COMMENT => Style.Italic
    case _ => Style.Ordinary
  }
}
