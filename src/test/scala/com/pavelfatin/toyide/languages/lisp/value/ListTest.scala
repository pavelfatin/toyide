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

class ListTest extends InterpreterTesting {
  @Test
  def emptyApplication() {
    assertError("()", "Empty application")
  }

  @Test
  def applicationToValue() {
    assertError("(1)", "Cannot apply to 1")
  }

  @Test
  def evaluationOrder() {
    assertOutput("((trace list) (trace 1) (trace 2))", "core.list12")
  }

  @Test
  def macroCaching() {
    val definition = "(def m (macro [x] (trace x))) "

    assertOutput(definition + "(m 1) (m 2)", "12")

    assertOutput(definition + "(def f (fn [x] (m x))) (f 1) (f 2)", "x")
  }
}