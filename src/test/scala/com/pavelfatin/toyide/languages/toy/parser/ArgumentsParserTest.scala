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