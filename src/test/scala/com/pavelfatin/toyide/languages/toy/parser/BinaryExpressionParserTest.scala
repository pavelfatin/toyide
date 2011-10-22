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

package com.pavelfatin.toyide.languages.toy.parser

import org.junit.Test

class BinaryExpressionParserTest extends ParserTest(ExpressionParser) {
  @Test
  def empty() {
    assertParsed("",
      """
      error: leaf
      """)
  }

  @Test
  def number() {
    assertParsed("1",
      """
      literal
        1
      """)
  }
  
  @Test
  def error() {
    assertParsed("=",
      """
      error: EQ
      """)
    
    assertParsed("1 + =",
      """
      binaryExpression
        literal
          1
        PLUS
        error: EQ
      """)
  }
  
  @Test
  def continuation() {
    assertParsed("1 =",
      """
      literal
        1
      """)
  }
  
  @Test
  def expected() {
    assertParsed("1 +",
      """
      binaryExpression
        literal
          1
        PLUS
        error: leaf
      """)
  }

  @Test
  def braces() {
    assertParsed("(1)",
      """
      group
        LPAREN
        literal
          1
        RPAREN
      """)
  }

  @Test
  def bracesEof() {
    assertParsed("(1",
      """
      group
        LPAREN
        literal
          1
        error: leaf
      """)
  }

  @Test
  def bracesUnexpected() {
    assertParsed("(1 true",
      """
      group
        LPAREN
        literal
          1
        error: true
      """)
  }

  @Test
  def multiplication() {
    assertParsed("1 * 2",
      """
      binaryExpression
        literal
          1
        STAR
        literal
          2
      """)
  }

  @Test
  def prefix() {
    assertParsed("-1",
      """
      prefixExpression
        MINUS
        literal
          1
      """)
  }

  @Test
  def prefixBoolean() {
    assertParsed("!false",
      """
      prefixExpression
        BANG
        literal
          false
      """)
  }

  @Test
  def prefixMultiple() {
    assertParsed("--1",
      """
      prefixExpression
        MINUS
        prefixExpression
          MINUS
          literal
            1
      """)
  }

  @Test
  def id() {
    assertParsed("foo * bar",
      """
      binaryExpression
        referenceToValue
          foo
        STAR
        referenceToValue
          bar
      """)
  }

  @Test
  def callExp() {
    assertParsed("foo(2 + 3) * bar",
      """
      binaryExpression
        callExpression
          referenceToFunction
            foo
          arguments
            LPAREN
            binaryExpression
              literal
                2
              PLUS
              literal
                3
            RPAREN
        STAR
        referenceToValue
          bar
      """)
  }

  @Test
  def division() {
    assertParsed("1 / 2",
      """
      binaryExpression
        literal
          1
        SLASH
        literal
          2
      """)
  }

  @Test
  def addition() {
    assertParsed("1 + 2",
      """
      binaryExpression
        literal
          1
        PLUS
        literal
          2
      """)
  }

  @Test
  def subtraction() {
    assertParsed("1 - 2",
      """
      binaryExpression
        literal
          1
        MINUS
        literal
          2
      """)
  }

  @Test
  def series() {
    assertParsed("1 + 2 - 3",
      """
      binaryExpression
        binaryExpression
          literal
            1
          PLUS
          literal
            2
        MINUS
        literal
          3
      """)
  }

  @Test
  def priority() {
    assertParsed("1 + 2 * 3",
      """
      binaryExpression
        literal
          1
        PLUS
        binaryExpression
          literal
            2
          STAR
          literal
            3
      """)

    assertParsed("1 * 2 + 3",
      """
      binaryExpression
        binaryExpression
          literal
            1
          STAR
          literal
            2
        PLUS
        literal
          3
      """)
    
    assertParsed("(1 * 2) + 3",
      """
      binaryExpression
        group
          LPAREN
          binaryExpression
            literal
              1
            STAR
            literal
              2
          RPAREN
        PLUS
        literal
          3
      """)

    assertParsed("1 * (2 + 3)",
      """
      binaryExpression
        literal
          1
        STAR
        group
          LPAREN
          binaryExpression
            literal
              2
            PLUS
            literal
              3
          RPAREN
      """)
  }

  @Test
  def relation() {
    assertParsed("1 + 2 < 3",
      """
      binaryExpression
        binaryExpression
          literal
            1
          PLUS
          literal
            2
        LT
        literal
          3
      """)
  }

  @Test
  def equality() {
    assertParsed("1 + 2 <= 3 == foo",
      """
      binaryExpression
        binaryExpression
          binaryExpression
            literal
              1
            PLUS
            literal
              2
          LT_EQ
          literal
            3
        EQ_EQ
        referenceToValue
          foo
      """)
  }

  @Test
  def boolean() {
    assertParsed("(true == false)",
      """
      group
        LPAREN
        binaryExpression
          literal
            true
          EQ_EQ
          literal
            false
        RPAREN
      """)
  }

  @Test
  def logical() {
    assertParsed("(true || false && true == false)",
      """
      group
        LPAREN
        binaryExpression
          literal
            true
          BAR_BAR
          binaryExpression
            literal
              false
            AMP_AMP
            binaryExpression
              literal
                true
              EQ_EQ
              literal
                false
        RPAREN
      """)
  }
}