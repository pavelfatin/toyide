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