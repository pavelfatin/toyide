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

class LogicTest extends InterpreterTesting {
  @Test
  def and() {
    assertValue("(and)", "true")

    assertValue("(and true)", "true")
    assertValue("(and false)", "false")

    assertValue("(and true true)", "true")
    assertValue("(and true false)", "false")
    assertValue("(and false true)", "false")
    assertValue("(and false false)", "false")

    assertValue("(and true true true)", "true")
    assertValue("(and false true true)", "false")
    assertValue("(and true false true)", "false")
    assertValue("(and true true false)", "false")

    assertOutput("(and (trace true) (trace false) (trace true))", "truefalse")

    assertValue("and", "core.and")
    assertValue("(def f and) (f true false)", "false")

    assertError("(and 1)")
  }

  @Test
  def or() {
    assertValue("(or)", "false")

    assertValue("(or true)", "true")
    assertValue("(or false)", "false")

    assertValue("(or true true)", "true")
    assertValue("(or true false)", "true")
    assertValue("(or false true)", "true")
    assertValue("(or false false)", "false")

    assertValue("(or false false false)", "false")
    assertValue("(or true false false)", "true")
    assertValue("(or false true false)", "true")
    assertValue("(or false false true)", "true")

    assertOutput("(or (trace false) (trace true) (trace false))", "falsetrue")

    assertValue("or", "core.or")
    assertValue("(def f or) (f true false)", "true")

    assertError("(or 1)")
  }

  @Test
  def not() {
    assertValue("(not false)", "true")
    assertValue("(not true)", "false")

    assertValue("not", "core.not")
    assertValue("(def f not) (f false)", "true")

    assertError("(not)")
    assertError("(not 1)")
    assertError("(not true true)")
  }
}