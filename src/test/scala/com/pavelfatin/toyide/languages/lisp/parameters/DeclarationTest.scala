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

class DeclarationTest extends InterpreterTesting {
  @Test
  def singular() {
    assertOK("(fn [])")
    assertOK("(fn [x])")
    assertOK("(fn [x y])")

    assertError("(fn)")
    assertError("(fn 1)")
    assertError("(fn [1])")
  }

  @Test
  def plural() {
    assertOK("(fn [&])")
    assertOK("(fn [x &])")
    assertOK("(fn [& x])")
    assertOK("(fn [x & y])")
    assertOK("(fn [x y & z])")

    assertError("(fn [& &])")
    assertError("(fn [x & &])")
    assertError("(fn [& x &])")
    assertError("(fn [& & x])")
    assertError("(fn [x & y &])")
  }

  @Test
  def duplicates() {
    assertError("(fn [x x])")
    assertError("(fn [x & x])")
  }

  @Test
  def underscore() {
    assertOK("(fn [_ _])")
    assertOK("(fn [_ & _])")
  }
}