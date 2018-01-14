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

class TypeMismatchTest extends InspectionTestBase(TypeMismatch) {
  @Test
  def variable() {
    assertMatches(marksIn("var v: integer = 1;")) {
      case Nil =>
    }

    assertMatches(marksIn("var v: integer = foo;")) {
      case Nil =>
    }

    val Message = TypeMismatch.Message("integer", "boolean")

    assertMatches(marksIn("var v: integer = true;")) {
      case MarkData(Text("true"), Message) :: Nil =>
    }
  }

  @Test
  def assignment() {
    assertMatches(marksIn("var v: integer = 1; v = 2;")) {
      case Nil =>
    }

    assertMatches(marksIn("var v: integer = 1; v = foo;")) {
      case Nil =>
    }


    val Message = TypeMismatch.Message("integer", "boolean")

    assertMatches(marksIn("var v: integer = 1; v = true;")) {
      case MarkData(Text("true"), Message) :: Nil =>
    }
  }

  @Test
  def ifStatement() {
    assertMatches(marksIn("if (true) {}")) {
      case Nil =>
    }

    assertMatches(marksIn("if (foo) {}")) {
      case Nil =>
    }

    val Message = TypeMismatch.Message("boolean", "integer")

    assertMatches(marksIn("if (1) {}")) {
      case MarkData(Text("1"), Message) :: Nil =>
    }
  }

  @Test
  def whileStatement() {
    assertMatches(marksIn("while (true) {}")) {
      case Nil =>
    }

    assertMatches(marksIn("while (foo) {}")) {
      case Nil =>
    }

    val Message = TypeMismatch.Message("boolean", "integer")

    assertMatches(marksIn("while (1) {}")) {
      case MarkData(Text("1"), Message) :: Nil =>
    }
  }

  @Test
  def returnStatement() {
    assertMatches(marksIn("def f(): integer = { return 1; }")) {
      case Nil =>
    }

    assertMatches(marksIn("def f(): integer = { return foo; }")) {
      case Nil =>
    }

    assertMatches(marksIn("return 1;")) {
      case Nil =>
    }

    val Message = TypeMismatch.Message("integer", "boolean")

    assertMatches(marksIn("def f(): integer = { return true; }")) {
      case MarkData(Text("true"), Message) :: Nil =>
    }
  }

  @Test
  def voidReturnStatement() {
    assertMatches(marksIn("def f(): void = { return; }")) {
      case Nil =>
    }

    assertMatches(marksIn("def f(): void = { return 1; }")) {
      case MarkData(Text("return 1;"), TypeMismatch.ReturnFromVoidFunctionMessage) :: Nil =>
    }

    assertMatches(marksIn("def f(): integer = { return; }")) {
      case MarkData(Text("return;"), TypeMismatch.MissingReturnValueMessage) :: Nil =>
    }
  }
}