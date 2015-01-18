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

class FunctionTest extends LibraryTestBase {
   @Test
   def apply() {
     assertValue("(apply + '(1 2))", "3")

     assertOutput("(apply print '((1 2) (3 4)))", "(1 2) (3 4)")
   }

  @Test
  def applyMacro() {
    assertValue("(apply (macro [& l] (cons 'list l)) '(1 2))", "(1 2)")

    assertValue("(apply (macro [& l] (cons 'list l)) '((list 1 2) (list 3 4)))", "((1 2) (3 4))")
  }

  @Test
   def identity() {
     assertValue("(identity 1)", "1")
   }

  @Test
  def const() {
    assertValue("((const 1) 2)", "1")
  }

   @Test
   def comp() {
     assertValue("((comp) 4)", "4")
     assertValue("((comp (fn [x] (* x 2))) 4)", "8")
     assertValue("((comp (fn [x] (* x 2)) (fn [x] (- x 1))) 4)", "6")
   }

   @Test
   def partial() {
     assertValue("((partial + 1 2) 4 5)", "12")
   }

   @Test
   def complement() {
     assertValue("((complement (fn [x] x)) true)", "false")
     assertValue("((complement (fn [x] x)) false)", "true")
   }
 }
