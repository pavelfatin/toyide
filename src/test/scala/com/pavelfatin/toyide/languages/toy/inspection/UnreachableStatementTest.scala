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

class UnreachableStatementTest extends InspectionTestBase(UnreachableStatement) {
  @Test
  def program() {
    assertMatches(marksIn("return; println();")) {
      case Nil =>
    }
  }

  @Test
  def returnIsLastStatement() {
    assertMatches(marksIn("def f(): void = { println(); return; }")) {
      case Nil =>
    }
  }

  @Test
  def returnIsNestedStatement() {
    assertMatches(marksIn("def f(): void = { if (false) { return; } println(); }")) {
      case Nil =>
    }
  }

  @Test
  def unreachableStatement() {
    assertMatches(marksIn("def f(): void = { return; println(); }")) {
      case MarkData(Text("println();"), UnreachableStatement.Message) :: Nil =>
    }
  }

  @Test
  def unreachableStatements() {
    assertMatches(marksIn("def f(): void = { return; println(); print(); }")) {
      case MarkData(Text("println();"), UnreachableStatement.Message) :: Nil =>
    }
  }

  @Test
  def unreachableStatementWithComplexReturn() {
    assertMatches(marksIn("def f(): void = { if (true) { return; } else { return; } println(); }")) {
      case MarkData(Text("println();"), UnreachableStatement.Message) :: Nil =>
    }
  }

  @Test
  def comment() {
    assertMatches(marksIn("def f(): void = { return; // comment\n }")) {
      case Nil =>
    }
    assertMatches(marksIn("def f(): void = { return; // comment 1\n //comment 2\n println(); }")) {
      case MarkData(Text("println();"), UnreachableStatement.Message) :: Nil =>
    }
  }
}