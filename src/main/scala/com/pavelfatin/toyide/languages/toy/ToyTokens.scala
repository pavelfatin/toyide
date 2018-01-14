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