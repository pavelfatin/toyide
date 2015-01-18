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

import com.pavelfatin.toyide.lexer.TokenKind

object LispTokens {
  val COMMENT = TokenKind("COMMENT", true)

  val COMMA = TokenKind("COMMA")

  val HASH = TokenKind("HASH")

  val QUOTE = TokenKind("QUOTE")
  val TILDE = TokenKind("TILDE")
  val TILDE_AT = TokenKind("TILDE_AT")
  val BACKQUOTE = TokenKind("BACKQUOTE")

  val Prefixes = Seq(QUOTE, TILDE, TILDE_AT, BACKQUOTE, HASH)

  val LPAREN = TokenKind("LPAREN")
  val RPAREN = TokenKind("RPAREN")

  val Parens = (LPAREN, RPAREN)

  val LBRACKET = TokenKind("LBRACKET")
  val RBRACKET = TokenKind("RBRACKET")

  val Brackets = (LBRACKET, RBRACKET)

  val INTEGER_LITERAL = TokenKind("INTEGER_LITERAL", true)
  val BOOLEAN_LITERAL = TokenKind("BOOLEAN_LITERAL", true)
  val STRING_LITERAL = TokenKind("STRING_LITERAL", true)
  val CHARACTER_LITERAL = TokenKind("CHARACTER_LITERAL", true)

  val Literals = Seq(INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, CHARACTER_LITERAL)

  val PREDEFINED_SYMBOL = TokenKind("PREDEFINED_SYMBOL", true)
  val CUSTOM_SYMBOL = TokenKind("CUSTOM_SYMBOL", true)

  val Symbols = Seq(PREDEFINED_SYMBOL, CUSTOM_SYMBOL)
}