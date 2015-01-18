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

class LispParserTest {
  @Test
  def empty() {
    assertParsed("",
      """
        |program
      """)
  }

  @Test
  def comment() {
    assertParsed("; foo",
      """
        |program
        |  comment
        |    ; foo
      """)
  }

  @Test
  def integerLiteral() {
    assertParsed("1",
      """
        |program
        |  integer
        |    1
      """)
    assertParsed("'1",
      """
        |program
        |  integer
        |    QUOTE
        |    1
      """)
  }

  @Test
  def booleanLiteral() {
    assertParsed("true",
      """
        |program
        |  boolean
        |    true
      """)
    assertParsed("'true",
      """
        |program
        |  boolean
        |    QUOTE
        |    true
      """)
  }

  @Test
  def characterLiteral() {
    assertParsed("\\c",
      """
        |program
        |  character
        |    \c
      """)
    assertParsed("'\\c",
      """
        |program
        |  character
        |    QUOTE
        |    \c
      """)
  }

  @Test
  def stringLiteral() {
    assertParsed("\"foo\"",
      """
        |program
        |  string
        |    "foo"
      """)
    assertParsed("'\"foo\"",
      """
        |program
        |  string
        |    QUOTE
        |    "foo"
      """)
  }

  @Test
  def predefinedSymbol() {
    assertParsed("let",
      """
        |program
        |  symbol
        |    let
      """)
    assertParsed("'let",
      """
        |program
        |  symbol
        |    QUOTE
        |    let
      """)
  }

  @Test
  def customSymbol() {
    assertParsed("name",
      """
        |program
        |  symbol
        |    name
      """)
    assertParsed("'name",
      """
        |program
        |  symbol
        |    QUOTE
        |    name
      """)
  }

  @Test
  def prefixes() {
    assertParsed("'1",
      """
        |program
        |  integer
        |    QUOTE
        |    1
      """)
    assertParsed("~1",
      """
        |program
        |  integer
        |    TILDE
        |    1
      """)
    assertParsed("~@1",
      """
        |program
        |  integer
        |    TILDE_AT
        |    1
      """)
    assertParsed("`1",
      """
        |program
        |  integer
        |    BACKQUOTE
        |    1
      """)
    assertParsed("#1",
      """
        |program
        |  integer
        |    HASH
        |    1
      """)
  }

  @Test
  def program() {
    assertParsed("1 2 3",
      """
        |program
        |  integer
        |    1
        |  integer
        |    2
        |  integer
        |    3
      """)
  }

  @Test
  def list() {
    assertParsed("()",
      """
        |program
        |  list
        |    LPAREN
        |    RPAREN
      """)

    assertParsed("(1)",
      """
        |program
        |  list
        |    LPAREN
        |    integer
        |      1
        |    RPAREN
      """)

    assertParsed("(1 2 3)",
      """
        |program
        |  list
        |    LPAREN
        |    integer
        |      1
        |    integer
        |      2
        |    integer
        |      3
        |    RPAREN
      """)
  }

  @Test
  def listPrefix() {
    assertParsed("'()",
      """
        |program
        |  list
        |    QUOTE
        |    LPAREN
        |    RPAREN
      """)
  }

  @Test
  def commasInList() {
    assertParsed("(1, 2, 3)",
      """
        |program
        |  list
        |    LPAREN
        |    integer
        |      1
        |    COMMA
        |    integer
        |      2
        |    COMMA
        |    integer
        |      3
        |    RPAREN
      """)
  }

  @Test
  def listAsVector() {
    assertParsed("[1]",
      """
        |program
        |  list
        |    LBRACKET
        |    integer
        |      1
        |    RBRACKET
      """)
  }

  @Test
  def unclosedList() {
    assertParsed("(",
      """
        |program
        |  list
        |    LPAREN
        |    error: leaf
      """)

    assertParsed("[",
      """
        |program
        |  list
        |    LBRACKET
        |    error: leaf
      """)
  }

  @Test
  def listBoundMismatch() {
    assertParsed("(]",
      """
        |program
        |  list
        |    LPAREN
        |    error: RBRACKET
      """)

    assertParsed("[)",
      """
        |program
        |  list
        |    LBRACKET
        |    error: RPAREN
      """)
  }

  private def assertParsed(code: String, expectation: String) {
    assertEquals(format(expectation), parsed(code))
  }

  private def parsed(code: String): String =
    LispParser.parse(LispLexer.analyze(code)).content.trim

  private def format(expectation: String) =
    expectation.replace("\r", "").trim.stripMargin
}