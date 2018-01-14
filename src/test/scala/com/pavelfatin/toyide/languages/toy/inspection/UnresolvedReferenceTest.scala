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