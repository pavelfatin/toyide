/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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