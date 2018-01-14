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

package com.pavelfatin.toyide.languages.toy

import org.junit.Test

abstract class ExpressionTestBase {
  @Test
  def literals() {
    assertOutput("print(\"foo\");", "foo")

    assertOutput("print(123);", "123")

    assertOutput("print(true);", "true")
    assertOutput("print(false);", "false")
  }

  @Test
  def booleanAnd() {
    assertOutput("print(true && true);", "true")
    assertOutput("print(true && false);", "false")
    assertOutput("print(false && true);", "false")
    assertOutput("print(false && false);", "false")
  }

  @Test
  def booleanAndLazyEvaluation() {
    assertOutput("def f(): boolean = { print(1); return true; } print(true && f());", "1true")
    assertOutput("def f(): boolean = { print(1); return true; } print(false && f());", "false")
  }

  @Test
  def booleanOr() {
    assertOutput("print(true || true);", "true")
    assertOutput("print(true || false);", "true")
    assertOutput("print(false || true);", "true")
    assertOutput("print(false || false);", "false")
  }

  @Test
  def booleanOrLazyEvaluation() {
    assertOutput("def f(): boolean = { print(1); return true; } print(true || f());", "true")
    assertOutput("def f(): boolean = { print(1); return true; } print(false || f());", "1true")
  }

  @Test
  def integerGt() {
    assertOutput("print(1 > 2);", "false")
    assertOutput("print(2 > 1);", "true")
    assertOutput("print(2 > 2);", "false")
  }

  @Test
  def integerGtEq() {
    assertOutput("print(1 >= 2);", "false")
    assertOutput("print(2 >= 1);", "true")
    assertOutput("print(2 >= 2);", "true")
  }

  @Test
  def integerLt() {
    assertOutput("print(1 < 2);", "true")
    assertOutput("print(2 < 1);", "false")
    assertOutput("print(2 < 2);", "false")
  }

  @Test
  def integerLtEq() {
    assertOutput("print(1 <= 2);", "true")
    assertOutput("print(2 <= 1);", "false")
    assertOutput("print(2 <= 2);", "true")
  }

  @Test
  def stringEq() {
    assertOutput("print(\"foo\" == \"foo\");", "true")
    assertOutput("print(\"foo\" == \"bar\");", "false")
  }

  @Test
  def stringEqNonConstant() {
    assertOutput("print(\"foo\" + 1 == \"foo2\");", "false")
    assertOutput("print(\"foo\" + 2 == \"foo2\");", "true")
  }

  @Test
  def integerEq() {
    assertOutput("print(1 == 1);", "true")
    assertOutput("print(1 == 2);", "false")
  }

  @Test
  def booleanEq() {
    assertOutput("print(true == true);", "true")
    assertOutput("print(false == false);", "true")
    assertOutput("print(true == false);", "false")
    assertOutput("print(false == true);", "false")
  }

  @Test
  def stringNotEq() {
    assertOutput("print(\"foo\" != \"foo\");", "false")
    assertOutput("print(\"foo\" != \"bar\");", "true")
  }

  @Test
  def stringNotEqNonConstant() {
    assertOutput("print(\"foo\" + 2 != \"foo2\");", "false")
    assertOutput("print(\"foo\" + 1 != \"foo2\");", "true")
  }

  @Test
  def integerNotEq() {
    assertOutput("print(1 != 1);", "false")
    assertOutput("print(1 != 2);", "true")
  }

  @Test
  def booleanNotEq() {
    assertOutput("print(true != true);", "false")
    assertOutput("print(false != false);", "false")
    assertOutput("print(true != false);", "true")
    assertOutput("print(false != true);", "true")
  }

  @Test
  def integerCalculations() {
    assertOutput("print(1 + 2);", "3")
    assertOutput("print(3 - 2);", "1")
    assertOutput("print(2 * 3);", "6")
    assertOutput("print(6 / 3);", "2")
    assertOutput("print(6 % 2);", "0")
    assertOutput("print(6 % 4);", "2")
  }

  @Test
  def prefixExpression() {
    assertOutput("print(+3);", "3")
    assertOutput("print(-3);", "-3")
    assertOutput("print(--3);", "3")

    assertOutput("print(!true);", "false")
    assertOutput("print(!false);", "true")
    assertOutput("print(!!true);", "true")
  }

  @Test
  def stringConcatenation() {
    assertOutput("print(\"foo\" + \"bar\");", "foobar")
    assertOutput("print(\"foo\" + 1);", "foo1")
    assertOutput("print(\"foo\" + true);", "footrue")
  }

  @Test
  def group() {
    assertOutput("print((1 + 2));", "3")
  }

  @Test
  def complexExpression() {
    assertOutput("print(1 + 2 * 3 * (4 + 5));", "55")
  }

  @Test
  def evaluationOrder() {
    assertOutput("""
      def a(): integer = { print(1); return 1; }
      def b(): integer = { print(2); return 2; }
      print(a() + b());
    """, "123")
  }

  protected def assertOutput(code: String, expected: String)
}