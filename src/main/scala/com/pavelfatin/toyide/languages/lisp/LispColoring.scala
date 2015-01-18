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

object LispColoring extends Coloring {
  def attributesFor(kind: TokenKind) = Attributes(colorFor(kind), None, weightFor(kind), styleFor(kind), false)

  private def colorFor(kind: TokenKind) = kind match {
    case COMMENT => new Color(128, 128, 128)
    case BOOLEAN_LITERAL => new Color(0, 0, 128)
    case INTEGER_LITERAL => new Color(0, 0, 255)
    case CHARACTER_LITERAL => new Color(0, 128, 0)
    case STRING_LITERAL => new Color(0, 128, 0)
    case PREDEFINED_SYMBOL => new Color(0, 0, 128)
    case _ => Color.BLACK
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

  def invert(attributes: Attributes) = attributes.copy(color = Color.WHITE)

  def highlight(attributes: Attributes) = attributes.copy(color = Color.RED)

  def dim(attributes: Attributes) = attributes.copy(color = Color.GRAY)
}
