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

class ParametersParserTest extends ParserTest(ParametersParser) {
  @Test
  def empty() {
    assertParsed("()",
      """
      parameters
        LPAREN
        RPAREN
      """)
  }

  @Test
  def incomplete() {
    assertParsed("(",
      """
      parameters
        LPAREN
        error: leaf
      """)
  }

  @Test
  def single() {
    assertParsed("(foo: integer)",
      """
      parameters
        LPAREN
        parameter
          foo
          typeSpec
            COLON
            INTEGER
        RPAREN
      """)
  }

  @Test
  def pair() {
    assertParsed("(foo: integer, bar: string)",
      """
      parameters
        LPAREN
        parameter
          foo
          typeSpec
            COLON
            INTEGER
        COMMA
        parameter
          bar
          typeSpec
            COLON
            STRING
        RPAREN
      """)
  }

  @Test
  def triple() {
    assertParsed("(foo: integer, bar: string, moo: boolean)",
      """
      parameters
        LPAREN
        parameter
          foo
          typeSpec
            COLON
            INTEGER
        COMMA
        parameter
          bar
          typeSpec
            COLON
            STRING
        COMMA
        parameter
          moo
          typeSpec
            COLON
            BOOLEAN
        RPAREN
      """)
  }
}