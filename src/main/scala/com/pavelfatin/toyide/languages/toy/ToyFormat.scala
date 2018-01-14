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

package com.pavelfatin.toyide.languages.toy

import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.lexer.TokenKind
import com.pavelfatin.toyide.formatter._
import com.pavelfatin.toyide.formatter.Distance._

object ToyFormat extends Format {
  def distanceFor(a: TokenKind, b: TokenKind) = (a, b) match {
    case (_, PLUS) => Space
    case (PLUS, _) => Space
    case (_, MINUS) => Space
    case (MINUS, _) => Space
    case (_, STAR) => Space
    case (STAR, _) => Space
    case (_, SLASH) => Space
    case (SLASH, _) => Space
    case (_, PERCENT) => Space
    case (PERCENT, _) => Space
    case (_, EQ) => Space
    case (EQ, _) => Space
    case (_, BANG_EQ) => Space
    case (BANG_EQ, _) => Space
    case (_, GT) => Space
    case (GT, _) => Space
    case (_, GT_EQ) => Space
    case (GT_EQ, _) => Space
    case (_, LT) => Space
    case (LT, _) => Space
    case (_, LT_EQ) => Space
    case (LT_EQ, _) => Space
    case (_, BAR_BAR) => Space
    case (BAR_BAR, _) => Space
    case (_, AMP_AMP) => Space
    case (AMP_AMP, _) => Space
    case (COLON, _) => Space
    case (COMMA, _) => Space
    case (_, EQ_EQ) => Space
    case (EQ_EQ, _) => Space
    case (_, ELSE) => Space
    case (_, LBRACE) => Space
    case (COMMENT, _) => Lines
    case (LBRACE, _) => Lines
    case (_, RBRACE) => Lines
    case (RBRACE, _) => Lines
    case (SEMI, _) => LinesOrSpace
    case (l, r) if Keywords.contains(l) => Space
    case _ => Joint
  }

  def indentDeltaFor(a: TokenKind, b: TokenKind) = (a, b) match {
    case (LBRACE, RBRACE) => 0
    case (_, ELSE) => 0
    case (LBRACE, _) => 1
    case (_, RBRACE) => -1
    case _ => 0
  }
}