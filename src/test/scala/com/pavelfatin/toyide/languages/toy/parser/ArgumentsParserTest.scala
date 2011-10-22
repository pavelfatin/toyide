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

class ArgumentsParserTest extends ParserTest(ArgumentsParser) {
  @Test
  def empty() {
    assertParsed("()",
      """
      arguments
        LPAREN
        RPAREN
      """)
  }

  @Test
  def incomplete() {
    assertParsed("(",
      """
      arguments
        LPAREN
        error: leaf
      """)
  }

  @Test
  def single() {
    assertParsed("(1)",
      """
      arguments
        LPAREN
        literal
          1
        RPAREN
      """)
  }

  @Test
  def pair() {
    assertParsed("(1, 2 + 3)",
      """
      arguments
        LPAREN
        literal
          1
        COMMA
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
  def triple() {
    assertParsed("(foo, 5, true)",
      """
      arguments
        LPAREN
        referenceToValue
          foo
        COMMA
        literal
          5
        COMMA
        literal
          true
        RPAREN
      """)
  }
}