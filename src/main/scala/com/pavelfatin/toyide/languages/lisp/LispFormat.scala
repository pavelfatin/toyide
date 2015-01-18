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

import com.pavelfatin.toyide.formatter.Distance._
import com.pavelfatin.toyide.formatter._
import com.pavelfatin.toyide.languages.lisp.LispTokens._
import com.pavelfatin.toyide.lexer.TokenKind

object LispFormat extends Format {
  def distanceFor(a: TokenKind, b: TokenKind) = (a, b) match {
    case (LPAREN | LBRACKET, _) => Joint
    case (_, RPAREN | RBRACKET) => Joint
    case (RPAREN | RBRACKET, LPAREN | LBRACKET) => Lines
    case (QUOTE | TILDE | TILDE_AT | BACKQUOTE | HASH, _) => Joint
    case (COMMENT, _) => Lines
    case _ => Space
  }

  def indentDeltaFor(a: TokenKind, b: TokenKind) = 0
}