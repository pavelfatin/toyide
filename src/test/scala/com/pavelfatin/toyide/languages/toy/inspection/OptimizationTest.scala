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

class OptimizationTest extends InspectionTestBase(Optimization) {
  @Test
  def literals() {
    assertMatches(marksIn("var v: integer = 1;")) {
      case Nil =>
    }

    assertMatches(marksIn("var v: string = \"foo\";")) {
      case Nil =>
    }
  }

  @Test
  def expression() {
    val Message = Optimization.Message("3")

    assertMatches(marksIn("var v: void = 1 + 2;")) {
        case MarkData(Text("1 + 2"), Message) :: Nil =>
    }
  }

  @Test
  def nestedExpression() {
    val Message = Optimization.Message("6")

    assertMatches(marksIn("var v: void = 1 + 2 + 3;")) {
        case MarkData(Text("1 + 2 + 3"), Message) :: Nil =>
    }
  }
}