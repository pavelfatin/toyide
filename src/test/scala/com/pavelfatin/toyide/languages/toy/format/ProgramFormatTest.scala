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