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

package com.pavelfatin.toyide.languages.toy.interpreter

import org.junit.{Assert, Test}
import com.pavelfatin.toyide.interpreter.{Place, EvaluationException}
import com.pavelfatin.toyide.languages.toy.EvaluationTestBase

class EvaluationTest extends EvaluationTestBase with InterpreterTesting {
  @Test
  def stackOverflow() {
    run("def f(): void = { f(); }")

    try {
      run("def f(): void = { f(); }; f();")
    } catch {
      case EvaluationException(message, _) if message == "Stack overflow" => return
    }

    Assert.fail("Expecting stack overflow exception")
  }

  @Test
  def stackOverflowWithParameterAllocations() {
    run("def f(p: integer): void = { f(1); }")

    try {
      run("def f(p: integer): void = { f(1); }; f(2);")
    } catch {
      case EvaluationException(message, _) if message == "Stack overflow" => return
    }

    Assert.fail("Expecting stack overflow exception")
  }

  @Test(expected = classOf[EvaluationException])
  def valueAllocationsInSingleScope() {
    run("var a: integer = 1; var a: boolean = true;")
  }

  @Test(expected = classOf[EvaluationException])
  def valueAllocationsInSingleScopeInFrame() {
    run("def f(): void = { var a: integer = 1; var a: boolean = true; }; f();")
  }

  @Test
  def valueAllocationsInDifferentScopes() {
    run("if (true) { var a: integer = 1; }; if (true) { var a: boolean = true; }")
    run("def f(): void = { if (true) { var a: integer = 1; }; if (true) { var a: boolean = true; } }; f();")
  }

  @Test
  def simpleTrace() {
    try {
      run("""
      print(1);
      print(2);
      print(3 / 0);
      print(4);
      """)
    } catch {
      case EvaluationException(_, trace) =>
        Assert.assertEquals(List(Place(None, 3)), trace.toList)
        return
    }

    Assert.fail("Expecting division by zero exception")
  }

  @Test
  def complexTrace() {
    try {
      run("""
      def a(): void = {
        print(1 / 0);
      }
      def b(): void = {
        a();
      }
      def c(): void = {
        b();
      }
      c();
      """)
    } catch {
      case EvaluationException(_, trace) =>
        val expected = List(
          Place(Some("a"), 2),
          Place(Some("b"), 5),
          Place(Some("c"), 8),
          Place(None, 10))

        Assert.assertEquals(expected, trace.toList)

        return
    }

    Assert.fail("Expecting division by zero exception")
  }
}