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

package com.pavelfatin.toyide.languages.lisp.core

import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
import org.junit.Test

class ConsoleTest extends InterpreterTesting {
  @Test
  def empty() {
    assertOutput("", "")
    assertOutput("1", "")
  }

  @Test
  def print() {
    assertOutput("(print)", "")
    assertOutput("(print 1 2 3)", "1 2 3")

    assertValue("(print 1)", "()")

    assertValue("print", "core.print")
    assertOutput("(def f print) (f 1)", "1")
  }

  @Test
  def println() {
    assertOutput("(println)", "\n")
    assertOutput("(println 1 2 3)", "1 2 3\n")

    assertValue("(println 1)", "()")

    assertValue("println", "core.println")
    assertOutput("(def f println) (f 1)", "1\n")
  }

  @Test
  def trace() {
    assertValue("(trace 1)", "1")
    assertValue("(trace true)", "true")
    assertValue("(trace \\c)", "\\c")
    assertValue("(trace (list 1 2 3))", "(1 2 3)")

    assertOutput("(trace 1)", "1")
    assertOutput("(trace true)", "true")
    assertOutput("(trace \\c)", "\\c")
    assertOutput("(trace (list 1 2 3))", "(1 2 3)")

    assertValue("trace", "core.trace")
    assertOutput("(def f trace) (f 1)", "1")

    assertError("(trace)")
    assertError("(trace 1 2)")
  }

  @Test
  def format() {
    assertValue("(format 123)", "(\\1 \\2 \\3)")
    assertOutput("(format 123)", "")
  }
}