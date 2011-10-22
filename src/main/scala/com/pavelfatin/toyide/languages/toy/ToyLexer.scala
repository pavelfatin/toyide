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

import com.pavelfatin.toyide.lexer.{Token, Lexer}
import com.pavelfatin.toyide.lexer.Tokens._
import ToyTokens._
import com.pavelfatin.toyide._

object ToyLexer extends Lexer {
  def analyze(input: CharSequence): Iterator[Token] = new TokensStream(input)
}

private class TokensStream(input: CharSequence) extends Iterator[Token] {
  private var index = 0
  private var marker = -1

  def next(): Token = {
    if (char == '=') {
      if(isAhead('=')) {
        return Token(EQ_EQ, captureChars(2))
      }
      return Token(EQ, captureChar)
    }

    if (char == '!' && isAhead('=')) return Token(BANG_EQ, captureChars(2))

    if (char == ':') return Token(COLON, captureChar)
    if (char == ';') return Token(SEMI, captureChar)
    if (char == ',') return Token(COMMA, captureChar)

    if (char == '+') return Token(PLUS, captureChar)
    if (char == '-') return Token(MINUS, captureChar)
    if (char == '*') return Token(STAR, captureChar)
    if (char == '/') {
      if(isAhead('/')) {
        mark()
        skip(c => c != '\n')
        return Token(COMMENT, marked)
      }
      return Token(SLASH, captureChar)
    }
    if (char == '%') return Token(PERCENT, captureChar)
    if (char == '!') return Token(BANG, captureChar)
    if (char == '<') {
      if(isAhead('=')) {
        return Token(LT_EQ, captureChars(2))
      }
      return Token(LT, captureChar)
    }
    if (char == '>')  {
      if(isAhead('=')) {
        return Token(GT_EQ, captureChars(2))
      }
      return Token(GT, captureChar)
    }

    if (char == '&' && isAhead('&')) return Token(AMP_AMP, captureChars(2))
    if (char == '|' && isAhead('|')) return Token(BAR_BAR, captureChars(2))

    if (char == '(') return Token(LPAREN, captureChar)
    if (char == ')') return Token(RPAREN, captureChar)

    if (char == '{') return Token(LBRACE, captureChar)
    if (char == '}') return Token(RBRACE, captureChar)

    if (char.isWhitespace) return Token(WS, capture(_.isWhitespace))

    if (char.isDigit) return Token(NUMBER_LITERAL, capture(_.isDigit))

    if (char == '"') {
      mark()
      advance()
      if (!hasNext) return Token(STRING_LITERAL, marked, Some("Unclosed string"))
      skip(c => c != '"' && c != '\n')
      if (!hasNext || char != '"') return Token(STRING_LITERAL, marked, Some("Unclosed string"))
      advance()
      return Token(STRING_LITERAL, marked)
    }

    if (char.isLetter) {
      val span = capture(_.isLetterOrDigit)
      val text = span.text

      if (text == "var") return Token(VAR, span)
      if (text == "def") return Token(DEF, span)
      if (text == "while") return Token(WHILE, span)
      if (text == "if") return Token(IF, span)
      if (text == "else") return Token(ELSE, span)
      if (text == "return") return Token(RETURN, span)

      if (text == "boolean") return Token(BOOLEAN, span)
      if (text == "string") return Token(STRING, span)
      if (text == "integer") return Token(INTEGER, span)
      if (text == "void") return Token(VOID, span)

      if (text == "true" || text == "false") return Token(BOOLEAN_LITERAL, span)

      return Token(IDENT, span)
    }

    Token(UNKNOWN, captureChar, Some("Unknown token"))
  }

  def advance() {
    index += 1
  }

  def mark() {
    marker = index
  }

  def marked = Span(input, marker, index)

  def captureChar = captureChars(1)

  def captureChars(count: Int) = {
    mark()
    Range(0, count).foreach(n => advance())
    marked
  }

  def skip(predicate: Char => Boolean) {
    while (hasNext && predicate(char)) advance()
  }

  def capture(predicate: Char => Boolean): Span = {
    mark()
    skip(predicate)
    marked
  }

  def char= input.charAt(index)

  def ahead(offset: Int) = {
    val i = index + offset
    if(i < input.length) Some(input.charAt(i)) else None
  }

  def isAhead(char: Char): Boolean = isAhead(_ == char)

  def isAhead(predicate: Char => Boolean): Boolean = ahead(1).filter(predicate).isDefined

  def hasNext = index < input.length
}


