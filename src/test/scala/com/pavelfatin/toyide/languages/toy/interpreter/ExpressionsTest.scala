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
import com.pavelfatin.toyide.interpreter.EvaluationException
import com.pavelfatin.toyide.languages.toy.ExpressionTestBase

class ExpressionsTest extends ExpressionTestBase with InterpreterTesting {
  @Test
  def divisionByZero() {
    try {
      run("print(1 / 0);")
    } catch {
      case EvaluationException(message, _) if message == "Division by zero" => return
    }

    Assert.fail("Expecting division by zero exception")
  }

  @Test
  def modulusWithZero() {
    try {
      run("print(1 % 0);")
    } catch {
      case EvaluationException(message, _) if message == "Division by zero" => return
    }

    Assert.fail("Expecting division by zero exception")
  }
}