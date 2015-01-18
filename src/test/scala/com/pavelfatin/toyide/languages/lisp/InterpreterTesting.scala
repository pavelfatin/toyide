/*
 * Copyright (C) 2014 Pavel Fatin <http://pavelfatin.com>
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

package com.pavelfatin.toyide.languages.lisp

import com.pavelfatin.toyide.Helpers._
import com.pavelfatin.toyide.MockConsole
import com.pavelfatin.toyide.interpreter.{EvaluationException, Value}
import com.pavelfatin.toyide.languages.lisp.node.ProgramNode
import com.pavelfatin.toyide.languages.lisp.value.{Environment, EnvironmentImpl}
import org.junit.Assert._

trait InterpreterTesting {
  protected def createEnvironment(): Environment = new EnvironmentImpl()

  protected def assertOutput(code: String, expected: String) {
    assertEquals(expected, run(code)._2)
  }

  protected def assertValue(code: String, expected: String) {
    assertEquals(expected, run(code)._1.presentation)
  }

  protected def assertOK(code: String) {
    run(code)
  }

  protected def assertError(code: String, expected: String = "") {
    try {
      run(code)
      fail("Error expected: " + expected)
    } catch {
      case EvaluationException(message, _) =>
        assertTrue("Expected: " + expected + ", actual: " + message, message.contains(expected))
    }
  }

  private def parse(code: String): ProgramNode = InterpreterTesting.parse(code.stripMargin)

  protected def run(code: String, environment: Environment = createEnvironment()): (Value, String) =
    InterpreterTesting.run(code.stripMargin, environment)
}

object InterpreterTesting {
  val Source = "Test"

  def parse(code: String): ProgramNode = {
    val root = LispParser.parse(LispLexer.analyze(code))
    val elements = root.elements
    assertNoProblemsIn(elements)
    root.asInstanceOf[ProgramNode]
  }

  def run(code: String, environment: Environment): (Value, String) = {
    val root = parse(code)
    val console = new MockConsole()
    val value = root.evaluate(Source, environment, console)
    (value, console.text)
  }
}
