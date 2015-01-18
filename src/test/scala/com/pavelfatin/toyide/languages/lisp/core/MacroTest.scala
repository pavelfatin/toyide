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

class MacroTest extends InterpreterTesting {
  @Test
  def eval() {
    assertValue("(eval 1)", "1")

    assertValue("(eval (quote (+ 1 2)))", "3")

    assertOutput("(eval (quote (print 1)))", "1")

    assertValue("(let [x 1] (eval x))", "1")

    assertError("(eval)")
    assertError("(eval 1 2)")
  }

  @Test
  def macroFunction() {
    assertValue("(macro [])", "macro0")
    assertValue("(macro [x])", "macro1")
    assertValue("(macro [x y])", "macro2")
    assertValue("(macro [] 1)", "macro0")
    assertValue("(macro [] 1 2)", "macro0")

    assertValue("(macro name [x y] 1 2)", "name_macro2")

    assertError("(macro)")
    assertError("(macro 1)")
    assertError("(macro 1 [])")
    assertError("(macro name)")
    assertError("(macro name 1)")

    assertValue("macro", "core.macro")
    assertValue("(def m macro) (m [x])", "macro1")
  }

  @Test
  def macroExpand() {
    assertValue("(macroexpand (quote ((macro [x] (list (quote print) x)) (+ 1 2))))", "(print (+ 1 2))")

    assertError("(macroexpand)")
    assertError("(macroexpand 1)")
    assertError("(macroexpand 1 2)")
  }
}