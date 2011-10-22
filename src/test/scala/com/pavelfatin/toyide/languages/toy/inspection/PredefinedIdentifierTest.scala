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

class PredefinedIdentifierTest extends InspectionTestBase(PredefinedIdentifier) {
  @Test
  def variable() {
    assertMatches(marksIn("var v: integer = 1;")) {
      case Nil =>
    }
    assertMatches(marksIn("var print: integer = 1;")) {
      case Nil =>
    }
    assertMatches(marksIn("var println: integer = 1;")) {
      case Nil =>
    }
  }

  @Test
  def parameter() {
    assertMatches(marksIn("def f(p: integer): integer = {}")) {
      case Nil =>
    }
    assertMatches(marksIn("def f(print: void): integer = {}")) {
      case Nil =>
    }
    assertMatches(marksIn("def f(println: void): integer = {}")) {
      case Nil =>
    }
  }

  @Test
  def function() {
    assertMatches(marksIn("def f(): integer = {}")) {
      case Nil =>
    }

    val Message1 = PredefinedIdentifier.Message("print")
    assertMatches(marksIn("def print(): void = {}")) {
      case MarkData(Text("print"), Message1) :: Nil =>
    }

    val Message2 = PredefinedIdentifier.Message("println")
    assertMatches(marksIn("def println(): void = {}")) {
      case MarkData(Text("println"), Message2) :: Nil =>
    }
  }
}