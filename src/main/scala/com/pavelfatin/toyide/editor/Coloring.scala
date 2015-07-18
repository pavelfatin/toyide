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

import java.awt.Color

import com.pavelfatin.toyide.Observable
import com.pavelfatin.toyide.lexer._

trait Coloring extends Function[String, Color] with Observable {
  def fontFamily: String

  def fontSize: Int

  def attributesFor(kind: TokenKind): Attributes
}

object Coloring {
  val TextForeground = "TextForeground"

  val TextBackground = "TextBackground"

  val CurrentLineBackground = "CurrentLineBackground"

  val CaretForeground = "CaretForeground"

  val SelectionForeground = "SelectionForeground"

  val SelectionBackground = "SelectionBackground"

  val HighlightBackground = "HighlightBackground"

  val HoverForeground = "HoverForeground"

  val PairedBraceBackground = "PairedBraceBackground"

  val UnbalancedBraceBackground = "UnbalancedBraceBackground"

  val RedForeground = "RedForeground"

  val UnderlineForeground = "UnderlineForeground"

  val DimForeground = "DimForeground"

  val FillBackground = "FillBackground"

  val BooleanLiteral = "BooleanLiteral"

  val IntegerLiteral = "IntegerLiteral"

  val CharLiteral = "CharLiteral"

  val StringLiteral = "StringLiteral"

  val Keyword = "Keyword"

  val Comment = "Comment"
}
