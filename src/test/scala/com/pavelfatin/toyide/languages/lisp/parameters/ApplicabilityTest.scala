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

class ApplicabilityTest extends InterpreterTesting {
   @Test
   def fnForm() {
     assertValue("((fn [a [b & c]] (list a b c)) 1 '(2 3 4))", "(1 2 (3 4))")
   }

   @Test
   def macroForm() {
     assertValue("((macro [a [b & c]] `(list ~a ~b (quote ~c))) 1 (2 3 4))", "(1 2 (3 4))")
   }

  @Test
  def letForm() {
    assertValue("(let [a 1 [b & c] '(2 3 4)] (list a b c))", "(1 2 (3 4))")

    assertValue("(let [& 1] &)", "1")

    assertError("(let [_ 1] _)")
  }
}