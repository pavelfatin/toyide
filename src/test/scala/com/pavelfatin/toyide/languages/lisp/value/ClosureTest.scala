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

class ClosureTest extends InterpreterTesting {
  @Test
  def accessibility() {
    assertValue("((let [x 1] (fn [] x)))", "1")
    assertValue("((let [x 1] (let [y 2] (fn [] (+ x y)))))", "3")

    assertValue("((let [x 1] (macro [] x)))", "1")
    assertValue("((let [x 1] (let [y 2] (macro [] (+ x y)))))", "3")
  }

  @Test
  def priority() {
    assertValue("((let [x 1] (fn [x] x)) 2)", "2")
    assertValue("((let [x 1] (let [x 2] (fn [] x))))", "2")
    assertValue("(let [x 1] ((let [x 2] (fn [] x))))", "2")

    assertValue("((let [x 1] (macro [x] x)) 2)", "2")
    assertValue("((let [x 1] (let [x 2] (macro [] x))))", "2")
    assertValue("(let [x 1] ((let [x 2] (macro [] x))))", "2")
  }
}