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

package com.pavelfatin.toyide.languages.lisp.parameters

import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
import org.junit.Test

class BindingTest extends InterpreterTesting {
  @Test
  def singular() {
    assertValue("((fn []))", "()")

    assertValue("((fn [x] x) 1)", "1")
    assertValue("((fn [x y] x) 1 2)", "1")
    assertValue("((fn [x y] y) 1 2)", "2")

    assertError("((fn []) 1)")
    assertError("((fn [x]) 1 2)")

    assertError("((fn [x]))")
    assertError("((fn [x y]) 1)")
  }

  @Test
  def plural() {
    assertValue("((fn [&]))", "()")

    assertValue("((fn [& x] x))", "()")
    assertValue("((fn [& x] x) 1)", "(1)")
    assertValue("((fn [& x] x) 1 2)", "(1 2)")

    assertValue("((fn [x & y] x) 1 2)", "1")
    assertValue("((fn [x & y] y) 1 2)", "(2)")

    assertValue("((fn [x y & z] x) 1 2 3 4)", "1")
    assertValue("((fn [x y & z] y) 1 2 3 4)", "2")
    assertValue("((fn [x y & z] z) 1 2 3 4)", "(3 4)")

    assertError("((fn [&]) 1)")
    assertError("((fn [x & y]))")
  }

  @Test
  def underscore() {
    assertValue("((fn [_ y] y) 1 2)", "2")

    assertError("((fn [_] _) 1)")
  }
}