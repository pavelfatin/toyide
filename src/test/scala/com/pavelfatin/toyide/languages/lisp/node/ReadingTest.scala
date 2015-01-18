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

package com.pavelfatin.toyide.languages.lisp.node

import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
import org.junit.Assert._
import org.junit._

class ReadingTest extends InterpreterTesting {
  @Test
  def expressions() {
    assertRead("1", "1")
    assertRead("true", "true")
    assertRead("\\c", "\\c")
    assertRead("()", "()")
    assertRead("symbol", "symbol")
  }

  @Test
  def specialCharacters() {
    assertRead("\\space", "\\space")
    assertRead("\\tab", "\\tab")
    assertRead("\\return", "\\return")
    assertRead("\\newline", "\\newline")
  }

  @Test
  def string() {
    assertRead("\"string\"", "(list \\s \\t \\r \\i \\n \\g)")
  }

  @Test
  def stringEscapes() {
    assertRead("\"\\t\"", "(list \\tab)")
    assertRead("\"\\r\"", "(list \\return)")
    assertRead("\"\\n\"", "(list \\newline)")
    assertRead("\"\\\"", "(list \\\\)")
  }

  @Test
  def quote() {
    assertRead("'symbol", "(quote symbol)")
  }

  @Test
  def unquote() {
    assertRead("~symbol", "(unquote symbol)")
  }

  @Test
  def unquoteSplicing() {
    assertRead("~@symbol", "(unquote-splicing symbol)")
  }

  @Test
  def quasiquote() {
    assertRead("`symbol", "(quasiquote symbol)")
  }

  @Test
  def withinList() {
    assertRead("('x ~y `z)", "((quote x) (unquote y) (quasiquote z))")
  }

  @Test
  def functionLiteral() {
    assertRead("#()", "(fn () ())")

    assertRead("#(%)", "(fn (_p1) (_p1))")
    assertRead("#(%1)", "(fn (_p1) (_p1))")
    assertRead("#(%2)", "(fn (_p1 _p2) (_p2))")

    assertRead("#(% %)", "(fn (_p1) (_p1 _p1))")
    assertRead("#(%1 %1)", "(fn (_p1) (_p1 _p1))")
    assertRead("#(%2 %2)", "(fn (_p1 _p2) (_p2 _p2))")

    assertRead("#(%1 %2)", "(fn (_p1 _p2) (_p1 _p2))")
    assertRead("#(%2 %1)", "(fn (_p1 _p2) (_p2 _p1))")

    assertRead("#(%&)", "(fn (& _ps) (_ps))")
    assertRead("#(%& %&)", "(fn (& _ps) (_ps _ps))")
    assertRead("#(% %&)", "(fn (_p1 & _ps) (_p1 _ps))")
    assertRead("#(%& %)", "(fn (_p1 & _ps) (_ps _p1))")

    assertRead("#(do %)", "(fn (_p1) (do _p1))")

    assertRead("#(do (do %))", "(fn (_p1) (do (do _p1)))")
  }

  private def assertRead(code: String, expected: String) {
    val root = InterpreterTesting.parse(code)
    val readable = root.elements.findBy[ReadableNode].getOrElse(
      throw new RuntimeException("No readable element found"))
    assertEquals(expected, readable.read(InterpreterTesting.Source).presentation)
  }
}