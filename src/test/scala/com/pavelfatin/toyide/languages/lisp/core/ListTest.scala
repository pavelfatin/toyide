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

class ListTest extends InterpreterTesting {
  @Test
  def list() {
    assertValue("(list)", "()")
    assertValue("(list 1)", "(1)")
    assertValue("(list 1 2 3)", "(1 2 3)")
    assertValue("(list 1 true \\c (list))", "(1 true \\c ())")

    assertOutput("(list (trace 1) (trace 2) (trace 3))", "123")

    assertValue("list", "core.list")
    assertValue("(def f list) (f 1 2 3)", "(1 2 3)")
  }

  @Test
  def cons() {
    assertValue("(cons 1 (list))", "(1)")
    assertValue("(cons 1 (cons 2 (list)))", "(1 2)")
    assertValue("(cons 1 (cons 2 (list 3)))", "(1 2 3)")

    assertOutput("(list (trace 1) (trace (list)))", "1()")

    assertValue("cons", "core.cons")
    assertValue("(def f cons) (f 1 (list))", "(1)")

    assertError("(cons)")
    assertError("(cons 1)")
    assertError("(cons (list))")
    assertError("(cons 1 2)")
    assertError("(cons (list) 2)")
    assertError("(cons 1 (list) 1)")
    assertError("(cons 1 (list) (list))")
  }

  @Test
  def first() {
    assertValue("(first (list 1))", "1")
    assertValue("(first (list 1 2))", "1")
    assertValue("(first (list 1 2 3))", "1")

    assertValue("first", "core.first")
    assertValue("(def f first) (f (list 1 2 3))", "1")

    assertError("(first (list))", "empty")

    assertError("(first)")
    assertError("(first 1)")
    assertError("(first (list 1) (list 1))")
  }

  @Test
  def rest() {
    assertValue("(rest (list 1))", "()")
    assertValue("(rest (list 1 2))", "(2)")
    assertValue("(rest (list 1 2 3))", "(2 3)")

    assertValue("rest", "core.rest")
    assertValue("(def f rest) (f (list 1 2 3))", "(2 3)")

    assertError("(rest (list))", "empty")

    assertError("(rest)")
    assertError("(rest 1)")
    assertError("(rest (list 1) (list 1))")
  }
}