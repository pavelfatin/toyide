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