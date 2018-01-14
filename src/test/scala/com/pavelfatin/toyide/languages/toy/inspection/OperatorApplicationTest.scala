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

class OperatorApplicationTest extends InspectionTestBase(OperatorApplication) {
  @Test
  def applicable() {
    assertMatches(marksIn("println(1 + 2);")) {
      case Nil =>
    }
  }

  @Test
  def unknownOperandsType() {
    assertMatches(marksIn("println(1 + v);")) {
      case Nil =>
    }
    assertMatches(marksIn("println(v + 1);")) {
      case Nil =>
    }
    assertMatches(marksIn("println(a + b);")) {
      case Nil =>
    }
  }

  @Test
  def inapplicable() {
    val Message = OperatorApplication.Message("+", "integer", "boolean")

    assertMatches(marksIn("println(1 + true);")) {
      case MarkData(Text("1 + true"), Message) :: Nil =>
    }
  }
}