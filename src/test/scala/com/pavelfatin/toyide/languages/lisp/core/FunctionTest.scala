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

class FunctionTest extends InterpreterTesting {
  @Test
  def fn() {
    assertValue("(fn [])", "fn0")
    assertValue("(fn [x])", "fn1")
    assertValue("(fn [x y])", "fn2")
    assertValue("(fn [] 1)", "fn0")
    assertValue("(fn [] 1 2)", "fn0")

    assertValue("(fn name [x y] 1 2)", "name_fn2")

    assertError("(fn)")
    assertError("(fn 1)")
    assertError("(fn 1 [])")
    assertError("(fn name)")
    assertError("(fn name 1)")

    assertValue("fn", "core.fn")
    assertValue("(def f fn) (f [x])", "fn1")
  }

  @Test
  def apply() {
    assertValue("(apply + '())", "0")
    assertValue("(apply + '(1))", "1")
    assertValue("(apply + '(1 2))", "3")

    assertError("(apply 1 '())")
    assertError("(apply + 1)")
    assertError("(apply + '() '())")

    assertValue("apply", "core.apply")
    assertValue("(def f apply) (f + '(1 2))", "3")
  }
}