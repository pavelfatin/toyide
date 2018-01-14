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

package com.pavelfatin.toyide.languages.toy.format

import com.pavelfatin.toyide.languages.toy.parser._
import org.junit.Test

class ProgramFormatTest extends FormatTestBase {
  // statement
  // block statement
  // program
  // empty

  @Test
  def statementSeparator() {
    assertFormatted("var a: integer = 1;var b: integer = 2;", ProgramParser,
      "var a: integer = 1; var b: integer = 2;")

    assertFormatted("var a: integer = 1; var b: integer = 2;", ProgramParser,
      "var a: integer = 1; var b: integer = 2;")

    assertFormatted("var a: integer = 1;\nvar b: integer = 2;", ProgramParser,
      "var a: integer = 1;\nvar b: integer = 2;")

    assertFormatted("var a: integer = 1;\n\nvar b: integer = 2;", ProgramParser,
      "var a: integer = 1;\n\nvar b: integer = 2;")
  }

  @Test
  def indentBraces() {
    assertFormatted("def f(): void = {}", ProgramParser, "def f(): void = {\n}")

    assertFormatted("def f(): void = {foo();}", ProgramParser, "def f(): void = {\n  foo();\n}")
    assertFormatted("def f(): void = {foo();\n}", ProgramParser, "def f(): void = {\n  foo();\n}")
    assertFormatted("def f(): void = {\nfoo();\n}", ProgramParser, "def f(): void = {\n  foo();\n}")
    assertFormatted("def f(): void = {\n  foo();\n}", ProgramParser, "def f(): void = {\n  foo();\n}")

    assertFormatted("def f(): void = {foo();}bar();", ProgramParser, "def f(): void = {\n  foo();\n}\nbar();")
    assertFormatted("def f(): void = {foo();}\nbar();", ProgramParser, "def f(): void = {\n  foo();\n}\nbar();")

    assertFormatted("def f(): void = {foo();\nbar();}", ProgramParser, "def f(): void = {\n  foo();\n  bar();\n}")
  }

  @Test
  def indentElse() {
    assertFormatted("if (true) {} else {}", ProgramParser, "if (true) {\n} else {\n}")
    assertFormatted("if (true) {} else {}\nfoo();", ProgramParser, "if (true) {\n} else {\n}\nfoo();")
  }

  @Test
  def nestedIndent() {
    assertFormatted("def f(): void = {if (true) {bar();}}", ProgramParser,
      "def f(): void = {\n  if (true) {\n    bar();\n  }\n}")
  }

  @Test
  def error() {
    assertFormatted("a=1;foo;b=2;", ProgramParser,
      "a = 1; foo; b = 2;", false)

    assertFormatted("var var", ProgramParser,
      "var var", false)
  }
}