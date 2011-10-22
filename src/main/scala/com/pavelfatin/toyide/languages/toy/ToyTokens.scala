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

package com.pavelfatin.toyide.languages.toy

import com.pavelfatin.toyide.lexer.TokenKind

object ToyTokens {
  val COMMENT = TokenKind("COMMENT", true)

  val COMMA = TokenKind("COMMA")
  val COLON = TokenKind("COLON")
  val SEMI = TokenKind("SEMI")

  val EQ = TokenKind("EQ")

  val LPAREN = TokenKind("LPAREN")
  val RPAREN = TokenKind("RPAREN")

  val LBRACE = TokenKind("LBRACE")
  val RBRACE = TokenKind("RBRACE")

  val PLUS = TokenKind("PLUS")
  val MINUS = TokenKind("MINUS")
  val STAR = TokenKind("STAR")
  val SLASH = TokenKind("SLASH")
  val PERCENT = TokenKind("PERCENT")

  val BANG = TokenKind("BANG")

  val AMP_AMP = TokenKind("AMP_AMP")
  val BAR_BAR = TokenKind("BAR_BAR")

  val EQ_EQ = TokenKind("EQ_EQ")
  val BANG_EQ = TokenKind("BANG_EQ")

  val LT = TokenKind("LT")
  val LT_EQ = TokenKind("LT_EQ")
  val GT = TokenKind("GT")
  val GT_EQ = TokenKind("GT_EQ")

  val VAR = TokenKind("VAR")
  val DEF = TokenKind("DEF")
  val WHILE = TokenKind("WHILE")
  val IF = TokenKind("IF")
  val ELSE = TokenKind("ELSE")
  val RETURN = TokenKind("RETURN")

  val Keywords = Seq(VAR, DEF, WHILE, IF, ELSE, RETURN)

  val STRING = TokenKind("STRING")
  val INTEGER = TokenKind("INTEGER")
  val BOOLEAN = TokenKind("BOOLEAN")
  val VOID = TokenKind("VOID")

  val Types = Seq(STRING, INTEGER, BOOLEAN, VOID)

  val NUMBER_LITERAL = TokenKind("NUMBER_LITERAL", true)
  val STRING_LITERAL = TokenKind("STRING_LITERAL", true)
  val BOOLEAN_LITERAL = TokenKind("BOOLEAN_LITERAL", true)

  val IDENT = TokenKind("IDENT", true)
}