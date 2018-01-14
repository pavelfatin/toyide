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
