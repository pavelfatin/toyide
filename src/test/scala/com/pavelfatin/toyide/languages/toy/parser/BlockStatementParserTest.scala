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

class BlockStatementParserTest extends ParserTest(BlockStatementParser) {
  @Test
  def variable() {
    assertParsed("var a: integer = 1;",
      """
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
      """)
  }

    @Test
  def assignment() {
      assertParsed("a = 1;",
          """
          assignment
            referenceToValue
              a
            EQ
            literal
              1
            SEMI
          """)
    }

  @Test
  def returnStatement() {
    assertParsed("return;",
      """
      return
        RETURN
        SEMI
      """)
  }

   @Test
  def whileStatement() {
     assertParsed("while (i > 0) { a = a + 1; }",
        """
        while
          WHILE
          LPAREN
          binaryExpression
            referenceToValue
              i
            GT
            literal
              0
          RPAREN
          block
            LBRACE
            assignment
              referenceToValue
                a
              EQ
              binaryExpression
                referenceToValue
                  a
                PLUS
                literal
                  1
              SEMI
            RBRACE
        """)
   }

  @Test
  def ifStatement() {
    assertParsed("if (i > 0) { a = a + 1; }",
      """
      if
        IF
        LPAREN
        binaryExpression
          referenceToValue
            i
          GT
          literal
            0
        RPAREN
        block
          LBRACE
          assignment
            referenceToValue
              a
            EQ
            binaryExpression
              referenceToValue
                a
              PLUS
              literal
                1
            SEMI
          RBRACE
      """)
  }

  @Test
  def call() {
    assertParsed("foo(1, 2);",
      """
      call
        callExpression
          referenceToFunction
            foo
          arguments
            LPAREN
            literal
              1
            COMMA
            literal
              2
            RPAREN
        SEMI
      """)
  }

  @Test
  def comment() {
    assertParsed("// foo bar",
      """
      comment
        // foo bar
      """)
  }

  @Test
  def empty() {
    assertParsed(";",
      """
      empty
        SEMI
      """)
  }

  @Test
  def wrong() {
    assertParsed("foo",
      """
      error: foo
      """)
  }
}