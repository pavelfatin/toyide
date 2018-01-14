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

class BlockParserTest extends ParserTest(BlockParser) {
  @Test
  def empty() {
    assertParsed("{}",
      """
      block
        LBRACE
        RBRACE
      """)
  }

  @Test
  def incomplete() {
    assertParsed("{",
      """
      block
        LBRACE
        error: leaf
      """)
  }

  @Test
  def variable() {
    assertParsed("{ var a: integer = 1; }",
      """
      block
        LBRACE
        variable
          VAR
          a
          typeSpec
            COLON
            INTEGER
          EQ
          literal
            1
          SEMI
        RBRACE
      """)
  }

  @Test
  def variables() {
    assertParsed("{ var a: integer = 1; var b: string = \"foo\"; }",
      """
      block
        LBRACE
        variable
          VAR
          a
          typeSpec
            COLON
            INTEGER
          EQ
          literal
            1
          SEMI
        variable
          VAR
          b
          typeSpec
            COLON
            STRING
          EQ
          literal
            "foo"
          SEMI
        RBRACE
      """)
  }

  @Test
  def assignment() {
    assertParsed("{ a = 1; }",
      """
      block
        LBRACE
        assignment
          referenceToValue
            a
          EQ
          literal
            1
          SEMI
        RBRACE
      """)
  }

  @Test
  def assignments() {
    assertParsed("{ a = 1; b = 2; }",
      """
      block
        LBRACE
        assignment
          referenceToValue
            a
          EQ
          literal
            1
          SEMI
        assignment
          referenceToValue
            b
          EQ
          literal
            2
          SEMI
        RBRACE
      """)
  }

  @Test
  def mixed() {
    assertParsed("{ b = 1; var a: integer = 2; a = 3; }",
      """
      block
        LBRACE
        assignment
          referenceToValue
            b
          EQ
          literal
            1
          SEMI
        variable
          VAR
          a
          typeSpec
            COLON
            INTEGER
          EQ
          literal
            2
          SEMI
        assignment
          referenceToValue
            a
          EQ
          literal
            3
          SEMI
        RBRACE
      """)
  }
}