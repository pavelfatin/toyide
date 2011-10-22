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

class UnresolvedReferenceTest extends InspectionTestBase(UnresolvedReference) {
  @Test
  def variable() {
    assertMatches(marksIn("var v: integer = 1; v = 2;")) {
      case Nil =>
    }

    val Message = UnresolvedReference.Message("value", "v")

    assertMatches(marksIn("v = 2;")) {
      case MarkData(Text("v"), Message) :: Nil =>
    }
  }

  @Test
  def parameter() {
    assertMatches(marksIn("def f(p: integer): integer = { var v: integer = p; }")) {
      case Nil =>
    }

    val Message = UnresolvedReference.Message("value", "p")

    assertMatches(marksIn("var v: integer = p;")) {
      case MarkData(Text("p"), Message) :: Nil =>
    }
  }

  @Test
  def function() {
    assertMatches(marksIn("def f(): integer = {}; f();")) {
      case Nil =>
    }

    val Message = UnresolvedReference.Message("function", "f")

    assertMatches(marksIn("f();")) {
      case MarkData(Text("f"), Message) :: Nil =>
    }
  }

  @Test
  def builtInFunctions() {
    assertMatches(marksIn("print();")) {
      case Nil =>
    }
    assertMatches(marksIn("println();")) {
      case Nil =>
    }
  }
}