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

package com.pavelfatin.toyide.languages.lisp.library

import org.junit.Test

class ArithmeticTest extends LibraryTestBase {
  @Test
  def inc() {
    assertValue("(inc 3)", "4")
  }

  @Test
  def dec() {
    assertValue("(dec 3)", "2")
  }

  @Test
  def even() {
    assertValue("(even? 1)", "false")
    assertValue("(even? 2)", "true")
  }

  @Test
  def odd() {
    assertValue("(odd? 1)", "true")
    assertValue("(odd? 2)", "false")
  }

  @Test
  def zero() {
    assertValue("(zero? 0)", "true")
    assertValue("(zero? 1)", "false")
  }

  @Test
  def pos() {
    assertValue("(pos? 1)", "true")
    assertValue("(pos? 0)", "false")
    assertValue("(pos? -1)", "false")
  }

  @Test
  def neg() {
    assertValue("(neg? -1)", "true")
    assertValue("(neg? 0)", "false")
    assertValue("(neg? 1)", "false")
  }

  @Test
  def sum() {
    assertValue("(sum nil)", "0")

    assertValue("(sum '(1 2))", "3")
  }

  @Test
  def product() {
    assertValue("(product '())", "1")

    assertValue("(product '(2 3))", "6")
  }
}
