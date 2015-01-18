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

import org.junit.Assert._
import org.junit.Test

class LispLexerTest {
  @Test
  def empty() {
    assertTokens("", "")
  }

  @Test
  def whitespace() {
    assertTokens(" ", "WS( )")
    assertTokens("  ", "WS(  )")
    assertTokens("\t", "WS(\t)")
    assertTokens("\n", "WS(\n)")
    assertTokens("\r\n", "WS(\r\n)")
    assertTokens("\t  \r\n \t ", "WS(\t  \r\n \t )")
  }

  @Test
  def comment() {
    assertTokens(";foo", "COMMENT(;foo)")
    assertTokens("; foo", "COMMENT(; foo)")
    assertTokens(";;", "COMMENT(;;)")
    assertTokens("; ; foo", "COMMENT(; ; foo)")
    assertTokens(";foo\n", "COMMENT(;foo), WS(\n)")
    assertTokens(";foo\n;bar", "COMMENT(;foo), WS(\n), COMMENT(;bar)")
  }

  @Test
  def integerLiteral() {
    assertTokens("1", "INTEGER_LITERAL(1)")
    assertTokens("123", "INTEGER_LITERAL(123)")

    assertTokens("-123", "INTEGER_LITERAL(-123)")

    assertTokens("-foo", "CUSTOM_SYMBOL(-foo)")
  }

  @Test
  def booleanLiteral() {
    assertTokens("true", "BOOLEAN_LITERAL(true)")
    assertTokens("false", "BOOLEAN_LITERAL(false)")
  }

  @Test
  def stringLiteral() {
    assertTokens("\"abc\"", "STRING_LITERAL(\"abc\")")
    assertTokens("\"a\"", "STRING_LITERAL(\"a\")")
    assertTokens("\"a\" \"b\"", "STRING_LITERAL(\"a\"), WS( ), STRING_LITERAL(\"b\")")
    assertTokens("\"123\"", "STRING_LITERAL(\"123\")")
    assertTokens("\"def\"", "STRING_LITERAL(\"def\")")
  }

  @Test
  def unclosedStringLiteral() {
    assertTokens("\"", "error: STRING_LITERAL(\")")
    assertTokens("\"a", "error: STRING_LITERAL(\"a)")
    assertTokens("\"abc", "error: STRING_LITERAL(\"abc)")

    assertTokens("\"\n", "error: STRING_LITERAL(\"), WS(\n)")
    assertTokens("\"abc\n", "error: STRING_LITERAL(\"abc), WS(\n)")

    assertTokens("\"abc\"\"", "STRING_LITERAL(\"abc\"), error: STRING_LITERAL(\")")
    assertTokens("\"a\"\"", "STRING_LITERAL(\"a\"), error: STRING_LITERAL(\")")
    assertTokens("\"\"\"", "STRING_LITERAL(\"\"), error: STRING_LITERAL(\")")
  }

  @Test
  def characterLiteral() {
    assertTokens("\\a", "CHARACTER_LITERAL(\\a)")

    assertTokens("\\space", "CHARACTER_LITERAL(\\space)")
    assertTokens("\\tab", "CHARACTER_LITERAL(\\tab)")
    assertTokens("\\return", "CHARACTER_LITERAL(\\return)")
    assertTokens("\\newline", "CHARACTER_LITERAL(\\newline)")

    assertTokens("\\newlin", "CHARACTER_LITERAL(\\n), CUSTOM_SYMBOL(ewlin)")
    assertTokens("\\newlinX", "CHARACTER_LITERAL(\\n), CUSTOM_SYMBOL(ewlinX)")
  }

  @Test
  def unclosedCharacterLiteral() {
    assertTokens("\\", "error: CHARACTER_LITERAL(\\)")
  }

  @Test
  def predefinedSymbol() {
    assertTokens("let", "PREDEFINED_SYMBOL(let)")
  }

  @Test
  def customSymbol() {
    assertTokens("name", "CUSTOM_SYMBOL(name)")
    assertTokens("name123", "CUSTOM_SYMBOL(name123)")
    assertTokens("name+-*/?><=&_%\'", "CUSTOM_SYMBOL(name+-*/?><=&_%\')")
    assertTokens("name1_a2'", "CUSTOM_SYMBOL(name1_a2')")

    assertTokens("let'", "CUSTOM_SYMBOL(let')")
    assertTokens("letlet", "CUSTOM_SYMBOL(letlet)")
  }

  @Test
  def char() {
    assertTokens(",", "COMMA(,)")
    assertTokens("#", "HASH(#)")
    assertTokens("'", "QUOTE(')")
    assertTokens("`", "BACKQUOTE(`)")
    assertTokens("(", "LPAREN(()")
    assertTokens(")", "RPAREN())")
    assertTokens("[", "LBRACKET([)")
    assertTokens("]", "RBRACKET(])")
  }

  @Test
  def tilde() {
    assertTokens("~", "TILDE(~)")
    assertTokens("~@", "TILDE_AT(~@)")

    assertTokens("~foo", "TILDE(~), CUSTOM_SYMBOL(foo)")
  }

  @Test
  def unknownChar() {
    assertTokens("{", "error: UNKNOWN({)")
  }

  @Test
  def sequence() {
    assertTokens("1 23  4", "INTEGER_LITERAL(1), WS( ), INTEGER_LITERAL(23), WS(  ), INTEGER_LITERAL(4)")
  }

  def assertTokens(input: String, expectation: String) {
    assertEquals(expectation, LispLexer.analyze(input).map(_.toCompleteString).mkString(", "))
  }
}