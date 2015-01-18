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

package com.pavelfatin.toyide.languages.lisp.node

import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.Helpers._
import com.pavelfatin.toyide.languages.lisp.{LispLexer, LispParser}
import com.pavelfatin.toyide.node.{Node, ReferenceNode}
import org.junit.Test

class ReferenceTest {
  @Test
  def unknown() {
    assertMatches(targetIn("x")) {
      case None =>
    }
  }

  @Test
  def fn() {
    assertMatches(targetIn("(fn [x])")) {
      case None =>
    }
    assertMatches(targetIn("(fn [x]) x")) {
      case None =>
    }
    assertMatches(targetIn("(fn [x] x)")) {
      case Some(Offset(5)) =>
    }
    assertMatches(targetIn("(fn [x y] y)")) {
      case Some(Offset(7)) =>
    }
    assertMatches(targetIn("(fn [[x]] x)")) {
      case Some(Offset(6)) =>
    }
    assertMatches(targetIn("(fn name [x] x)")) {
      case Some(Offset(10)) =>
    }
    assertMatches(targetIn("(fn name [] name)")) {
      case None =>
    }
    assertMatches(targetIn("(fn name []) name")) {
      case None =>
    }
  }

  @Test
  def macroFn() {
    assertMatches(targetIn("(macro [x])")) {
      case None =>
    }
    assertMatches(targetIn("(macro [x]) x")) {
      case None =>
    }
    assertMatches(targetIn("(macro [x] x)")) {
      case Some(Offset(8)) =>
    }
    assertMatches(targetIn("(macro [x y] y)")) {
      case Some(Offset(10)) =>
    }
    assertMatches(targetIn("(macro [[x]] x)")) {
      case Some(Offset(9)) =>
    }
    assertMatches(targetIn("(macro name [x] x)")) {
      case Some(Offset(13)) =>
    }
    assertMatches(targetIn("(macro name [] name)")) {
      case None =>
    }
    assertMatches(targetIn("(macro name []) name")) {
      case None =>
    }
  }

  @Test
  def loop() {
    assertMatches(targetIn("(loop [x 1])")) {
      case None =>
    }
    assertMatches(targetIn("(loop [x 1]) x")) {
      case None =>
    }
    assertMatches(targetIn("(loop [x y]) y")) {
      case None =>
    }
    assertMatches(targetIn("(loop [x] x)")) {
      case Some(Offset(7)) =>
    }
    assertMatches(targetIn("(loop [x 1 y 2] y)")) {
      case Some(Offset(11)) =>
    }
    assertMatches(targetIn("(loop [[x] '(1)] x)")) {
      case Some(Offset(8)) =>
    }
    assertMatches(targetIn("(loop [x x])")) {
      case Some(Offset(7)) =>
    }
  }

  @Test
  def let() {
    assertMatches(targetIn("(let [x 1])")) {
      case None =>
    }
    assertMatches(targetIn("(let [x 1]) x")) {
      case None =>
    }
    assertMatches(targetIn("(let [x y]) y")) {
      case None =>
    }
    assertMatches(targetIn("(let [x 1] x)")) {
      case Some(Offset(6)) =>
    }
    assertMatches(targetIn("(let [x 1 y 2] y)")) {
      case Some(Offset(10)) =>
    }
    assertMatches(targetIn("(let [[x] '(1)] x)")) {
      case Some(Offset(7)) =>
    }
    assertMatches(targetIn("(let [x x])")) {
      case Some(Offset(6)) =>
    }
  }

  @Test
  def ifLet() {
    assertMatches(targetIn("(if-let [x 1])")) {
      case None =>
    }
    assertMatches(targetIn("(if-let [x 1]) x")) {
      case None =>
    }
    assertMatches(targetIn("(if-let [x y]) y")) {
      case None =>
    }
    assertMatches(targetIn("(if-let [x 1] x)")) {
      case Some(Offset(9)) =>
    }
    assertMatches(targetIn("(if-let [x 1 y 2] y)")) {
      case Some(Offset(13)) =>
    }
    assertMatches(targetIn("(if-let [[x] '(1)] x)")) {
      case Some(Offset(10)) =>
    }
    assertMatches(targetIn("(if-let [x x])")) {
      case Some(Offset(9)) =>
    }
  }

  @Test
  def define() {
    assertMatches(targetIn("(def x 1)")) {
      case None =>
    }
    assertMatches(targetIn("(def x x)")) {
      case Some(Offset(5)) =>
    }
    assertMatches(targetIn("(def x 1) x")) {
      case Some(Offset(5)) =>
    }
  }

  @Test
  def defn() {
    assertMatches(targetIn("(defn f [])")) {
      case None =>
    }
    assertMatches(targetIn("(defn f []) f")) {
      case Some(Offset(6)) =>
    }

    assertMatches(targetIn("(defn f [x])")) {
      case None =>
    }
    assertMatches(targetIn("(defn f [x]) x")) {
      case None =>
    }
    assertMatches(targetIn("(defn f [x] x)")) {
      case Some(Offset(9)) =>
    }
    assertMatches(targetIn("(defn f [x y] y)")) {
      case Some(Offset(11)) =>
    }
    assertMatches(targetIn("(defn f [[x]] x)")) {
      case Some(Offset(10)) =>
    }
  }

  @Test
  def defmacro() {
    assertMatches(targetIn("(defmacro m [])")) {
      case None =>
    }
    assertMatches(targetIn("(defmacro m []) m")) {
      case Some(Offset(10)) =>
    }

    assertMatches(targetIn("(defmacro m [x])")) {
      case None =>
    }
    assertMatches(targetIn("(defmacro m [x]) x")) {
      case None =>
    }
    assertMatches(targetIn("(defmacro m [x] x)")) {
      case Some(Offset(13)) =>
    }
    assertMatches(targetIn("(defmacro m [x y] y)")) {
      case Some(Offset(15)) =>
    }
    assertMatches(targetIn("(defmacro m [[x]] x)")) {
      case Some(Offset(14)) =>
    }
  }

  @Test
  def precedence() {
    assertMatches(targetIn("(fn [x] (fn [x] x))")) {
      case Some(Offset(13)) =>
    }
    assertMatches(targetIn("(def x 1) (fn [x] x)")) {
      case Some(Offset(15)) =>
    }
    assertMatches(targetIn("(def x 1) (def x 1) x")) {
      case Some(Offset(5)) =>
    }
  }

  private def referenceIn(code: String): Option[ReferenceNode] = {
    val elements = LispParser.parse(LispLexer.analyze(code)).elements
    assertNoProblemsIn(elements)
    elements.filterBy[SymbolNode].find(_.target.isDefined)
  }

  private def targetIn(code: String): Option[Node] = {
    referenceIn(code).flatMap(_.target)
  }
}