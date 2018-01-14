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

package com.pavelfatin.toyide.languages.toy.node

import org.junit.Test
import com.pavelfatin.toyide.languages.toy.ToyType._

class ExpressionTypeTest extends ExpressionTypeTestBase {
  @Test
  def literals() {
    assertTypeIs("\"foo\"", StringType)
    assertTypeIs("1", IntegerType)
    assertTypeIs("true", BooleanType)
  }

  @Test
  def reduce() {
    assertTypeIs("\"foo\" + 1", StringType)
    assertTypeIs("1 + 2", IntegerType)
    assertNoType("1 + \"foo\"")
    assertTypeIs("\"foo\" + 1 + 2 + \"bar\"", StringType)
    assertNoType("1 + 2 + \"bar\"")
  }

  @Test
  def addition() {
    assertTypeIs("1 + 2", IntegerType)
    assertTypeIs("1 - 2", IntegerType)
    assertTypeIs("\"foo\" + \"foo\"", StringType)
    assertNoType("\"foo\" - \"foo\"")
    assertTypeIs("\"foo\" + 1", StringType)
    assertNoType("\"foo\" - 1")
    assertNoType("1 + \"foo\"")
    assertNoType("1 - \"foo\"")
    assertNoType("true + true")
    assertNoType("1 + true")
    assertNoType("1 - true")
    assertTypeIs("\"foo\" + true", StringType)
    assertNoType("\"foo\" - true")
    assertNoType("true + \"foo\"")
    assertNoType("true - \"foo\"")
  }

  @Test
  def predefinedCall() {
    assertTypeIs("print(1);", VoidType)
    assertTypeIs("println(1);", VoidType)
  }
}