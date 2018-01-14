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

class OptimizationTest extends InspectionTestBase(Optimization) {
  @Test
  def literals() {
    assertMatches(marksIn("var v: integer = 1;")) {
      case Nil =>
    }

    assertMatches(marksIn("var v: string = \"foo\";")) {
      case Nil =>
    }
  }

  @Test
  def expression() {
    val Message = Optimization.Message("3")

    assertMatches(marksIn("var v: void = 1 + 2;")) {
        case MarkData(Text("1 + 2"), Message) :: Nil =>
    }
  }

  @Test
  def nestedExpression() {
    val Message = Optimization.Message("6")

    assertMatches(marksIn("var v: void = 1 + 2 + 3;")) {
        case MarkData(Text("1 + 2 + 3"), Message) :: Nil =>
    }
  }
}