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

class DefinitionTest extends InterpreterTesting {
  @Test
  def definition() {
    assertValue("(def x 1)", "()")

    assertError("(def x)")
    assertError("(def x 1 2)")

    assertValue("(def x 1) x", "1")

    assertOutput("(def x (trace 1))", "1")
    assertOutput("(def x (trace 1)) x x", "1")

    assertValue("(def x 1) (def y 2) x", "1")
    assertValue("(def x 1) (def y 2) y", "2")

    assertValue("(def x 1) (def x 2) x", "2")

    assertValue("def", "core.def")
    assertValue("(def f def) (f x 1) x", "1")

    assertError("x (def x 1)")
    assertError("(def x 1) y")

    assertValue("(def x 1) (def f (fn [] x)) (f)", "1")
    assertValue("(def f (fn [] (def x 1))) (f) x", "1")
    assertError("(def f (fn [] (def x 1))) x", "")
  }

  @Test
  def let() {
    assertValue("(let [x 1])", "()")

    assertValue("(let [x 1] x)", "1")
    assertValue("(let [x 1 y 2] x)", "1")
    assertValue("(let [x 1 y 2] y)", "2")
    assertValue("(let [] 1 2)", "2")

    assertValue("(let [x 1 y [+ x 2]] y)", "3")
    assertError("(let [x [+ y 2] y 1] x)")

    assertError("(let)")
    assertError("(let 1)")
    assertError("(let [x])")
    assertError("(let [x 1 y])")

    assertOutput("(let [] (print 1) (print 2))", "12")

    assertOutput("(let [x [print 1]])", "1")
    assertOutput("(let [x [print 1] y [print 2]])", "12")

    assertOutput("(let [x [trace 1]] x x)", "1")

    assertValue("let", "core.let")
    assertValue("(def f let) (f (x 1) x)", "1")

    assertError("(let [x 1]) x")
    assertError("(let [x 1] y)")
  }
}