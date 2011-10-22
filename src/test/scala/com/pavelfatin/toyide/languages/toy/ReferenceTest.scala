/*
 * Copyright (C) 2011 Pavel Fatin <http://pavelfatin.com>
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

package com.pavelfatin.toyide.languages.toy

import org.junit.Test
import parser.ProgramParser
import com.pavelfatin.toyide.node.{Node, ReferenceNode}
import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.Helpers._

class ReferenceTest {
  @Test
  def noDeclarations() {
    assertMatches(targetIn("v = 1;")) {
      case None =>
    }
    assertMatches(targetIn("def foo(): integer = { p = 1; }")) {
      case None =>
    }
    assertMatches(targetIn("f();")) {
      case None =>
    }
  }

  @Test
  def nameDiffers() {
    assertMatches(targetIn("var x: integer = 1; v = 1;")) {
      case None =>
    }
    assertMatches(targetIn("def foo(x: integer): integer = { p = 1; }")) {
      case None =>
    }
    assertMatches(targetIn("def x(): integer = {}; f();")) {
      case None =>
    }
  }

  @Test
  def declared() {
    assertMatches(targetIn("var v: integer = 1; v = 1;")) {
      case Some(Offset(0)) =>
    }
    assertMatches(targetIn("def foo(p: integer): integer = { p = 1; }")) {
      case Some(Offset(8)) =>
    }
    assertMatches(targetIn("def f(): integer = {}; f();")) {
      case Some(Offset(0)) =>
    }
  }

  @Test
  def severalDeclarations() {
    assertMatches(targetIn("var v: integer = 1; var x: integer = 1; v = 1;")) {
      case Some(Offset(0)) =>
    }
    assertMatches(targetIn("def foo(p: integer, x: integer): integer = { p = 1; }")) {
      case Some(Offset(8)) =>
    }
    assertMatches(targetIn("def f(): integer = {}; def x(): integer = {}; f();")) {
      case Some(Offset(0)) =>
    }
  }

  @Test
  def severalDeclarationsReversed() {
    assertMatches(targetIn("var x: integer = 1; var v: integer = 1; v = 1;")) {
      case Some(Offset(20)) =>
    }
    assertMatches(targetIn("def foo(x: integer, p: integer): integer = { p = 1; }")) {
      case Some(Offset(20)) =>
    }
    assertMatches(targetIn("def x(): integer = {}; def f(): integer = {}; f();")) {
      case Some(Offset(23)) =>
    }
  }

  @Test
  def incorrectOrder() {
    assertMatches(targetIn("v = 1; var v: integer = 1;")) {
      case None =>
    }
    assertMatches(targetIn("p = 1; def foo(p: integer): integer = {}")) {
      case None =>
    }
    assertMatches(targetIn("f(); def f(): integer = {}")) {
      case None =>
    }
  }

  @Test
  def incorrectKind() {
    assertMatches(targetIn("var v: integer = 1; v();")) {
      case None =>
    }
    assertMatches(targetIn("def foo(p: integer): integer = { p(); }")) {
      case None =>
    }
    assertMatches(targetIn("def f(): integer = {}; f = 1;")) {
      case None =>
    }
  }

  @Test
  def outerScope() {
    assertMatches(targetIn("var v: integer = 1; while (true) { v = 1; }")) {
      case Some(Offset(0)) =>
    }
    assertMatches(targetIn("def foo(p: integer): integer = { while (true) { p = 1; } }")) {
      case Some(Offset(8)) =>
    }
    assertMatches(targetIn("def f(): integer = {}; while (true) { f(); }")) {
      case Some(Offset(0)) =>
    }
  }

  @Test
  def innerScope() {
    assertMatches(targetIn("while (true) { var a: integer = 1; }; a = 1;")) {
      case None =>
    }
    assertMatches(targetIn("def foo(p: integer): integer = {};  p = 1;")) {
      case None =>
    }
  }

  @Test
  def scopePreference() {
    assertMatches(targetIn("var v: integer = 1; while (true) { var v: integer = 1; v = 1; }")) {
      case Some(Offset(35)) =>
    }
    assertMatches(targetIn("var p: integer = 1; def f(p: integer): integer = { p = 1; };")) {
      case Some(Offset(26)) =>
    }
  }

  @Test
  def selfScope() {
    assertMatches(targetIn("def f(): integer = { f(); }")) {
      case Some(Offset(0)) =>
    }
  }

  def targetIn(code: String): Option[Node] = {
    val elements = ProgramParser.parse(ToyLexer.analyze(code)).elements
    assertNoProblemsIn(elements)
    elements.findBy[ReferenceNode].flatMap(_.target)
  }
}