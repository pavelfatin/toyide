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

class QuotingTest extends InterpreterTesting {
  @Test
  def quote() {
    assertValue("(quote 1)", "1")
    assertValue("(quote true)", "true")
    assertValue("(quote \\c)", "\\c")

    assertValue("(quote symbol)", "symbol")

    assertValue("(quote ())", "()")
    assertValue("(quote (symbol))", "(symbol)")
    assertValue("(quote (symbol 1 2 3))", "(symbol 1 2 3)")
    assertValue("(quote (symbol (1 2 3)))", "(symbol (1 2 3))")

    assertValue("(quote (print 1))", "(print 1)")
    assertOutput("(quote (print 1))", "")

    assertValue("quote", "core.quote")
    assertValue("(def f quote) (f symbol)", "symbol")

    assertError("(quote)")
    assertError("(quote 1 2)")
  }

  @Test
  def unquote() {
    assertValue("unquote", "core.unquote")

    assertError("(unquote)")
    assertError("(unquote 1)")
    assertError("(unquote 1 2)")
  }

  @Test
  def quasiquote() {
    assertValue("(quasiquote 1)", "1")
    assertValue("(quasiquote true)", "true")
    assertValue("(quasiquote \\c)", "\\c")

    assertValue("(quasiquote symbol)", "symbol")

    assertValue("(quasiquote ())", "()")
    assertValue("(quasiquote (symbol))", "(symbol)")
    assertValue("(quasiquote (symbol 1 2 3))", "(symbol 1 2 3)")
    assertValue("(quasiquote (symbol (1 2 3)))", "(symbol (1 2 3))")

    assertValue("(quasiquote (print 1))", "(print 1)")
    assertOutput("(quasiquote (print 1))", "")

    assertValue("quasiquote", "core.quasiquote")
    assertValue("(def f quasiquote) (f symbol)", "symbol")

    assertError("(quasiquote)")
    assertError("(quasiquote 1 2)")
  }

  @Test
  def unquoting() {
    assertValue("(quasiquote (unquote 1))", "1")
    assertValue("(quasiquote (unquote (+ 1 2)))", "3")

    assertValue("(quasiquote (symbol (+ 1 2)))", "(symbol (+ 1 2))")
    assertValue("(quasiquote (symbol (unquote (+ 1 2))))", "(symbol 3)")
  }

  @Test
  def unquoteSplicing() {
    assertValue("(quasiquote ((list 1 2)))", "((list 1 2))")
    assertValue("(quasiquote ((unquote-splicing (list 1 2))))", "(1 2)")

    assertValue("(quasiquote (symbol (list 1 2)))", "(symbol (list 1 2))")
    assertValue("(quasiquote (symbol (unquote-splicing (list 1 2))))", "(symbol 1 2)")

    assertError("(quasiquote (unquote-splicing (list 1 2)))")
    assertError("(quasiquote (symbol (unquote-splicing 1)))")
  }

  @Test
  def uniqueSymbolGeneration() {
    assertValue("(gensym \"a\")", "a_0")

    assertValue("(list (gensym \"a\") (gensym \"a\"))", "(a_0 a_1)")

    assertValue("(list (gensym \"a\") (gensym \"b\"))", "(a_0 b_1)")
  }

  @Test
  def autoUniqueSymbolGeneration() {
    assertValue("(quasiquote a#)", "a_0")
    assertValue("(quasiquote (a#))", "(a_0)")
    assertValue("(quasiquote (a# a#))", "(a_0 a_0)")
    assertValue("(quasiquote (a# (a#)))", "(a_0 (a_0))")
    assertValue("(quasiquote ((a#) a#))", "((a_0) a_0)")
    assertValue("(quasiquote ((a#) (a#)))", "((a_0) (a_0))")

    assertValue("(quasiquote (a# b#))", "(a_0 b_1)")

    assertValue("(list (quasiquote a#) (quasiquote a#))", "(a_0 a_1)")

    assertValue("(quasiquote a)", "a")

    assertValue("(quote a#)", "a#")
  }
}