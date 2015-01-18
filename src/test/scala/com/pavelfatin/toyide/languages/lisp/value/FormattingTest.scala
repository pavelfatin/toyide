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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
import org.junit.Test

class FormattingTest extends InterpreterTesting {
  @Test
  def empty() {
    assertOutput("(print)", "")
  }

  @Test
  def integer() {
    assertOutput("(print 1)", "1")
  }

  @Test
  def boolean() {
    assertOutput("(print true)", "true")
  }

  @Test
  def character() {
    assertOutput("(print \\c)", "\\c")
    assertOutput("(print \\space)", "\\space")
    assertOutput("(print \\tab)", "\\tab")
    assertOutput("(print \\return)", "\\return")
    assertOutput("(print \\newline)", "\\newline")
  }

  @Test
  def list() {
    assertOutput("(print (list))", "()")
    assertOutput("(print (list 1 2 3))", "(1 2 3)")
  }

  @Test
  def string() {
    assertOutput("(print \"string\")", "string")
  }

  @Test
  def multiple() {
    assertOutput("(print 1 2 3)", "1 2 3")
    assertOutput("(print true false true)", "true false true")
    assertOutput("(print \\a \\b \\c)", "\\a \\b \\c")
    assertOutput("(print (list 1) (list 2) (list 3))", "(1) (2) (3)")
    assertOutput("(print \"a\" \"b\" \"c\")", "a b c")

    assertOutput("(print 1 true \\a (list 2) \"b\")", "1 true \\a (2) b")
  }
}