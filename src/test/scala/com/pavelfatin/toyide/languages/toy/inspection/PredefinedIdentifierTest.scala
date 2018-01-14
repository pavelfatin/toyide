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