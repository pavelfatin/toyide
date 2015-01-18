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

class FlowTest extends InterpreterTesting {
  @Test
  def program() {
    assertValue("", "()")
    assertValue("1", "1")
    assertValue("1 2 3", "3")

    assertOutput("(print 1) (print 2) (print 3)", "123")
  }

  @Test
  def doForm() {
    assertValue("(do)", "()")
    assertValue("(do 1)", "1")
    assertValue("(do 1 2 3)", "3")

    assertOutput("(do (print 1) (print 2) (print 3))", "123")

    assertValue("do", "core.do")
    assertValue("(def f do) (f 1)", "1")
  }

  @Test
  def ifForm() {
    assertValue("(if true 1)", "1")
    assertValue("(if false 1)", "()")

    assertOutput("(if true (print 1))", "1")
    assertOutput("(if false (print 1))", "")

    assertOutput("(if (trace true) (print 1))", "true1")
  }

  @Test
  def ifElse() {
    assertValue("(if true 1 2)", "1")
    assertValue("(if false 1 2)", "2")

    assertOutput("(if true (print 1) (print 2))", "1")
    assertOutput("(if false (print 1) (print 2))", "2")

    assertOutput("(if (trace true) (print 1) (print 2))", "true1")
  }

  @Test
  def ifCondition() {
    assertValue("(if '() 1 2)", "2")
    assertValue("(if '(1 2 3) 1 2)", "1")

    assertValue("(if 0 1 2)", "1")
    assertValue("(if \\c 1 2)", "1")
  }

  @Test
  def ifValue() {
    assertValue("if", "core.if")
    assertValue("(def f if) (f true 1)", "1")
  }

  @Test
  def ifErrors() {
    assertError("(if)")
    assertError("(if true)")
    assertError("(if true 1 2 3)")
  }

  @Test
  def error() {
    assertError("(error \"foo\")", "foo")
    assertError("(error 1)", "1")

    assertError("(error 1 2)", "1 2")

    assertError("(error)", "")

    assertValue("error", "core.error")
  }

  @Test
  def loop() {
    assertValue("(loop [])", "()")
    assertValue("(loop [] 1)", "1")
    assertValue("(loop [] 1 2)", "2")

    assertValue("(loop [x 1] x)", "1")
    assertValue("(loop [x 1 y 2] x)", "1")
    assertValue("(loop [x 1 y 2] y)", "2")

    assertValue("(loop [x 1 y (* x 2)] y)", "2")
  }

  @Test
  def recurInLoop() {
    assertOutput("(loop [x 1] (if (< x 4) (do (print x) (recur (+ x 1)))))", "123")

    assertOutput("(loop [x 1 y (* x 2)] (if (< x 4) (do (print y) (recur (+ x 1) (+ x 1)))))", "223")

    assertError("(loop [x 1] (recur))", "arguments")
  }

  @Test
  def recurInFunction() {
    assertOutput("((fn [x] (if (< x 4) (do (print x) (recur (+ x 1))))) 1)", "123")

    assertOutput("((fn [x y] (if (< x 4) (do (print y) (recur (+ x 1) (+ x 1))))) 1 2)", "223")

    assertError("((fn [x] (recur)) 1)", "arguments")
  }
}