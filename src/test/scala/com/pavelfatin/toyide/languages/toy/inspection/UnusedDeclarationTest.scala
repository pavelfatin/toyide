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