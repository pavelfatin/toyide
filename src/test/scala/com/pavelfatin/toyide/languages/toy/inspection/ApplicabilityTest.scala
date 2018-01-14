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

class ApplicabilityTest extends InspectionTestBase(Applicability) {
  @Test
  def fine() {
    assertMatches(marksIn("def f(): void = {}; f();")) {
      case Nil =>
    }
    assertMatches(marksIn("def f(a: integer, b: boolean): void = {}; f(1, true);")) {
      case Nil =>
    }
    assertMatches(marksIn("println(1, true);")) {
      case Nil =>
    }
  }

  @Test
  def excessive() {
    val Message1 = Applicability.Excessive("f(): void")

    assertMatches(marksIn("def f(): void = {}; f(1);")) {
      case MarkData(Text("1"), Message1) :: Nil =>
    }

    val Message2 = Applicability.Excessive("f(a: integer, b: integer): void")

    assertMatches(marksIn("def f(a: integer, b: integer): void = {}; f(1, 2, 3, 4);")) {
      case MarkData(Text("3"), Message2) :: MarkData(Text("4"), Message2) :: Nil =>
    }
  }

  @Test
  def missed() {
    val Message1 = Applicability.Missed("f(a: integer): void", "a")

    assertMatches(marksIn("def f(a: integer): void = {}; f();")) {
      case MarkData(Text(")"), Message1) :: Nil =>
    }

    val Message2 = Applicability.Missed("f(a: integer, b: integer, c: integer, d: integer): void", "c, d")

    assertMatches(marksIn("def f(a: integer, b: integer, c: integer, d: integer): void = {}; f(1, 2);")) {
      case MarkData(Text(")"), Message2) :: Nil =>
    }
  }

  @Test
  def mismatch() {
    val Message1 = Applicability.Mismatch("integer", "boolean")

    assertMatches(marksIn("def f(a: integer): void = {}; f(true);")) {
      case MarkData(Text("true"), Message1) :: Nil =>
    }

    val Message2 = Applicability.Mismatch("boolean", "integer")

    assertMatches(marksIn("def f(a: integer, b: boolean): void = {}; f(true, 1);")) {
      case MarkData(Text("true"), Message1) :: MarkData(Text("1"), Message2) :: Nil =>
    }
  }

  @Test
  def voidToPredefined() {
    assertMatches(marksIn("print(print());")) {
      case MarkData(Text("print()"), Applicability.Void) :: Nil =>
    }

    assertMatches(marksIn("print(print(1), print(2));")) {
      case MarkData(Text("print(1)"), Applicability.Void) :: MarkData(Text("print(2)"), Applicability.Void) :: Nil =>
    }
  }
}