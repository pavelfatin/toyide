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

package com.pavelfatin.toyide.languages.toy.compiler

import org.junit.{Assert, Test}
import com.pavelfatin.toyide.languages.toy.parser.ProgramParser
import com.pavelfatin.toyide.languages.toy.{ToyLexer, EvaluationTestBase}
import com.pavelfatin.toyide.compiler._
import com.pavelfatin.toyide.MockConsole

class TranslationTest extends EvaluationTestBase with TranslatorTesting {
  @Test(expected = classOf[TranslationException])
  def translationError() {
    val root = ProgramParser.parse(ToyLexer.analyze("var a: integer = ;"))
    root.translate("Main", new Labels()).toText("Main")
  }

  def customClassName() {
    val root = ProgramParser.parse(ToyLexer.analyze("var a: integer = 1; print(a);"))
    val bytecode = Assembler.assemble(root, "Foo")
    val output = new MockConsole()
    BytecodeInvoker.invoke(bytecode, "Foo", output)
    Assert.assertEquals("1", output.text)
  }

  @Test
  def localsWithLargeIndex() {
    assertOutput("def f(a: integer, b: integer, c: integer): void = { var i: integer = 1; }; f(1, 2, 3); ", "")
    assertOutput("def f(a: integer, b: integer, c: integer, d: integer): void = { print(d); }; f(1, 2, 3, 4); ", "4")
    assertOutput("def f(a: integer, b: integer, c: integer, d: integer): void = { d = 1; }; f(1, 2, 3, 4); ", "")
  }

  @Test
  def stackOverflow() {
    run("def f(): void = { f(); }")

    try {
      run("def f(): void = { f(); }; f();")
    } catch {
      case InvocationException(message, _) if message == "java.lang.StackOverflowError" => return
    }

    Assert.fail("Expecting stack overflow exception")
  }

  @Test
  def stackOverflowWithParameterAllocations() {
    run("def f(p: integer): void = { f(1); }")

    try {
      run("def f(p: integer): void = { f(1); }; f(2);")
    } catch {
      case InvocationException(message, _) if message == "java.lang.StackOverflowError" => return
    }

    Assert.fail("Expecting stack overflow exception")
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
      case InvocationException(_, trace) =>
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
      case InvocationException(_, trace) =>
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