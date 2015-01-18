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

class ArithmeticTest extends InterpreterTesting {
  @Test
  def add() {
    assertValue("(+)", "0")

    assertValue("(+ 1)", "1")
    assertValue("(+ 1 2)", "3")
    assertValue("(+ 1 2 3)", "6")

    assertOutput("(+ (trace 1) (trace 2) (trace 3))", "123")

    assertValue("+", "core.+")
    assertValue("(def f +) (f 1 2)", "3")

    assertError("(+ true)")
  }

  @Test
  def sub() {
    assertValue("(- 1)", "-1")

    assertValue("(- 3 1)", "2")
    assertValue("(- 3 2 5)", "-4")

    assertOutput("(- (trace 1) (trace 2) (trace 3))", "123")

    assertValue("-", "core.-")
    assertValue("(def f -) (f 1 2)", "-1")

    assertError("(-)")
    assertError("(- true)")
  }

  @Test
  def mul() {
    assertValue("(*)", "1")

    assertValue("(* 2)", "2")
    assertValue("(* 2 3)", "6")
    assertValue("(* 2 3 4)", "24")

    assertOutput("(* (trace 1) (trace 2) (trace 3))", "123")

    assertValue("*", "core.*")
    assertValue("(def f *) (f 2 3)", "6")

    assertError("(* true)")
  }

  @Test
  def div() {
    assertValue("(/ 6 3)", "2")
    assertValue("(/ 6 3 2 )", "1")

    assertValue("(/ 0 1)", "0")

    assertOutput("(/ (trace 1) (trace 2) (trace 3))", "123")

    assertValue("/", "core./")
    assertValue("(def f /) (f 6 3)", "2")

    assertError("(/)")
    assertError("(/ 1 0)", "zero")
    assertError("(/ 1 0 2)", "zero")
    assertError("(/ true)")
  }

  @Test
  def mod() {
    assertValue("(mod 5 3)", "2")
    assertValue("(mod 6 3)", "0")

    assertValue("(mod 0 1)", "0")

    assertOutput("(mod (trace 1) (trace 2))", "12")

    assertValue("mod", "core.mod")
    assertValue("(def f mod) (f 5 3)", "2")

    assertError("(mod)")
    assertError("(mod 1 2 3)")
    assertError("(mod 1 0)", "zero")
    assertError("(mod true true)")
  }
}