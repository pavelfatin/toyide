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