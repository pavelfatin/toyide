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

class ComparisonTest extends InterpreterTesting {
  @Test
  def gt() {
    assertValue("(> 1 2)", "false")
    assertValue("(> 2 1)", "true")
    assertValue("(> 1 1)", "false")

    assertOutput("(> (trace 1) (trace 2))", "12")

    assertValue(">", "core.>")
    assertValue("(def f >) (f 1 2)", "false")

    assertError("(> 1 true)")
    assertError("(> true 2)")
    assertError("(> true true)")
    assertError("(>)")
    assertError("(> 1)")
    assertError("(> 1 2 3)")
  }

  @Test
  def gtEq() {
    assertValue("(>= 1 2)", "false")
    assertValue("(>= 2 1)", "true")
    assertValue("(>= 1 1)", "true")

    assertOutput("(>= (trace 1) (trace 2))", "12")

    assertValue(">=", "core.>=")
    assertValue("(def f >=) (f 1 2)", "false")

    assertError("(>= 1 true)")
    assertError("(>= true 2)")
    assertError("(>= true true)")
    assertError("(>=)")
    assertError("(>= 1)")
    assertError("(>= 1 2 3)")
  }

  @Test
  def lt() {
    assertValue("(< 1 2)", "true")
    assertValue("(< 2 1)", "false")
    assertValue("(< 1 1)", "false")

    assertOutput("(< (trace 1) (trace 2))", "12")

    assertValue("<", "core.<")
    assertValue("(def f <) (f 1 2)", "true")

    assertError("(< 1 true)")
    assertError("(< true 2)")
    assertError("(< true true)")
    assertError("(<)")
    assertError("(< 1)")
    assertError("(< 1 2 3)")
  }

  @Test
  def ltEq() {
    assertValue("(<= 1 2)", "true")
    assertValue("(<= 2 1)", "false")
    assertValue("(<= 1 1)", "true")

    assertOutput("(<= (trace 1) (trace 2))", "12")

    assertValue("<=", "core.<=")
    assertValue("(def f <=) (f 1 2)", "true")

    assertError("(<= 1 true)")
    assertError("(<= true 2)")
    assertError("(<= true true)")
    assertError("(<=)")
    assertError("(<= 1)")
    assertError("(<= 1 2 3)")
  }

  @Test
  def eq() {
    assertValue("(= 1 2)", "false")
    assertValue("(= 2 1)", "false")
    assertValue("(= 1 1)", "true")

    assertValue("(= false true)", "false")
    assertValue("(= true false)", "false")
    assertValue("(= true true)", "true")
    assertValue("(= false false)", "true")

    assertValue("(= \\a \\b)", "false")
    assertValue("(= \\a \\a)", "true")

    assertValue("(= '() '())", "true")
    assertValue("(= '() '(1))", "false")
    assertValue("(= '(1) '(1))", "true")
    assertValue("(= '(1) '(2))", "false")
    assertValue("(= '(1) '(1 2))", "false")
    assertValue("(= '(1 2) '(1))", "false")
    assertValue("(= '(1 2) '(1 2))", "true")

    assertValue("(= '(1 (2 3)) '(1 (2 3)))", "true")
    assertValue("(= '(1 (2 3)) '(1 (2 4)))", "false")

    assertOutput("(= (trace 1) (trace 2))", "12")

    assertValue("=", "core.=")
    assertValue("(def f =) (f 1 2)", "false")

    assertError("(= 1 true)")
    assertError("(= true 2)")
    assertError("(= '(1) '(true))")
    assertError("(=)")
    assertError("(= 1)")
    assertError("(= 1 2 3)")
  }
}