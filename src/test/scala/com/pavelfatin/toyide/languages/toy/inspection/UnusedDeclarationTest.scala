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

package com.pavelfatin.toyide.languages.toy.inspection

import org.junit.Test
import com.pavelfatin.toyide.inspection.MarkData
import com.pavelfatin.toyide.Helpers._

class UnusedDeclarationTest extends InspectionTestBase(UnusedDeclaration) {
  @Test
  def variable() {
    assertMatches(marksIn("var v: integer = 1; println(v);")) {
      case Nil =>
    }

    val Message = UnusedDeclaration.Message("variable", "v")

    assertMatches(marksIn("var v: integer = 1;")) {
      case MarkData(Text("v"), Message) :: Nil =>
    }
  }

  @Test
  def function() {
    assertMatches(marksIn("def f(): integer = {}; println(f());")) {
      case Nil =>
    }

    val Message = UnusedDeclaration.Message("function", "f")

    assertMatches(marksIn("def f(): integer = {};")) {
      case MarkData(Text("f"), Message) :: Nil =>
    }
  }

  @Test
  def parameter() {
    assertMatches(marksIn("def f(p: integer): void = { println(p); }; println(f());")) {
      case Nil =>
    }

    val Message = UnusedDeclaration.Message("parameter", "p")

    assertMatches(marksIn("def f(p: integer): void = {}; println(f());")) {
      case MarkData(Text("p"), Message) :: Nil =>
    }
  }

  @Test
  def order() {
    val Message = UnusedDeclaration.Message("variable", "v")

    assertMatches(marksIn("println(v); var v: integer = 1;")) {
      case MarkData(Text("v"), Message) :: Nil =>
    }
  }

  @Test
  def nestedScope() {
    assertMatches(marksIn("var v: integer = 1; if (true) { println(v); }")) {
      case Nil =>
    }
    assertMatches(marksIn("var v: integer = 1; if (false) {} else { println(v); }")) {
      case Nil =>
    }
    assertMatches(marksIn("var v: integer = 1; while (true) { println(v); }")) {
      case Nil =>
    }
    assertMatches(marksIn("var v: integer = 1; def f(): void = { println(v); }; println(f());")) {
      case Nil =>
    }
  }

  @Test
  def selfUsage() {
    val Message = UnusedDeclaration.Message("function", "f")

    assertMatches(marksIn("def f(): integer = { println(f()); }")) {
      case MarkData(Text("f"), Message) :: Nil =>
    }
  }
}