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

class CoreTest extends LibraryTestBase {
  @Test
  def defmacro() {
    assertValue("(macroexpand '(defmacro m [x] 1))", "(def m (macro m (x) 1))")
  }

  @Test
  def defn() {
    assertValue("(macroexpand '(defn f [x] 1))", "(def f (fn f (x) 1))")
  }

  @Test
  def defnPloy() {
    assertValue("(defn-poly f ([] 1)) (f)", "1")
    assertValue("(defn-poly f ([x] x)) (f 1)", "1")
    assertValue("(defn-poly f ([x y] x)) (f 1 2)", "1")
    assertValue("(defn-poly f ([x y] y)) (f 1 2)", "2")

    assertValue("(defn-poly f ([x] x) ([x y] (+ x y))) (f 1)", "1")
    assertValue("(defn-poly f ([x] x) ([x y] (+ x y))) (f 1 2)", "3")

    assertError("(defn-poly f ([x y] x)) (f 1)", "argument count")
    assertError("(defn-poly f ([x y] x)) (f 1 2 3)", "argument count")
  }

  @Test
  def isTrue() {
    assertValue("(true? true)", "true")
    assertValue("(true? false)", "false")

    assertValue("(true? 1)", "false")
  }

  @Test
  def isFalse() {
    assertValue("(false? true)", "false")
    assertValue("(false? false)", "true")

    assertValue("(false? 1)", "false")
  }

  @Test
  def when() {
    assertValue("(when true)", "()")

    assertValue("(when true 1)", "1")
    assertValue("(when true 1 2)", "2")

    assertValue("(when false 1)", "()")

    assertOutput("(when true (trace 1) (trace 2))", "12")
    assertOutput("(when false (trace 1) (trace 2))", "")
  }

  @Test
  def cond() {
    assertValue("(cond)", "()")

    assertValue("(cond true 1)", "1")
    assertValue("(cond false 1)", "()")

    assertValue("(cond true 1 true 2)", "1")
    assertValue("(cond false 1 true 2)", "2")
    assertValue("(cond false 1 false 2)", "()")

    assertOutput("(cond (trace true) (trace 1) (trace true) (trace 2))", "true1")
  }

  @Test
  def elseForm() {
    assertValue("else", "true")
  }

  @Test
  def ifLet() {
    assertValue("(if-let [x true] 1)", "1")
    assertValue("(if-let [x false] 1)", "()")

    assertValue("(if-let [x true] 1 2)", "1")
    assertValue("(if-let [x false] 1 2)", "2")

    assertValue("(if-let [x '(1 2 3)] 1 2)", "1")
    assertValue("(if-let [x nil] 1 2)", "2")

    assertOutput("(if-let [x (trace true)] (trace 1) (trace 2))", "true1")
    assertOutput("(if-let [x (trace false)] (trace 1) (trace 2))", "false2")
  }

  @Test
  def threadSecond() {
    assertValue("(-> 4)", "4")
    assertValue("(-> 4 inc)", "5")

    assertValue("(-> 4 inc list)", "(5)")

    assertValue("(-> 6 (/ 2))", "3")
    assertValue("(-> 12 (/ 2) (/ 3))", "2")

    assertValue("(-> 12 (/ 2 3))", "2")
  }

  @Test
  def threadLast() {
    assertValue("(->> 4)", "4")
    assertValue("(->> 4 inc)", "5")

    assertValue("(->> 4 inc list)", "(5)")

    assertValue("(->> 2 (/ 6))", "3")
    assertValue("(->> 2 (/ 6) (/ 12))", "4")

    assertValue("(->> 3 (/ 12 2))", "2")
  }
}
