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

package com.pavelfatin.toyide.optimizer

import com.pavelfatin.toyide.languages.toy.parser.ProgramParser
import com.pavelfatin.toyide.languages.toy.ToyLexer
import com.pavelfatin.toyide.Helpers._
import com.pavelfatin.toyide.document.DocumentImpl
import org.junit.{Assert, Test}

class ToyOptimizationTest {
  @Test
  def literals() {
    assertOptimized("print(1);", "print(1);")
    assertOptimized("print(\"foo\");", "print(\"foo\");")
    assertOptimized("print(true);", "print(true);")
  }

  @Test
  def prefixExpresion() {
    assertOptimized("print(!true);", "print(false);")
    assertOptimized("print(!!true);", "print(true);")

    assertOptimized("print(!a);", "print(!a);")
  }

  @Test
  def groupExpresion() {
    assertOptimized("print((1));", "print(1);")
  }

  @Test
  def binaryExpresion() {
    assertOptimized("print(1 + 2);", "print(3);")

    assertOptimized("print(1 + a);", "print(1 + a);")
  }

  @Test
  def nestedExpresions() {
    assertOptimized("print(1 + 2 + 3);", "print(6);")
  }
  
  @Test
  def multipleExpresions() {
    assertOptimized("print(1 + 2); print(3 + 4);", "print(3); print(7);")
  }

  @Test
  def stingExpression() {
    assertOptimized("print(\"foo\" + \" \" + 1);", "print(\"foo 1\");")
  }

  @Test
  def binaryExpresionLazy() {
    val a = "var a: boolean = true; "

    assertOptimized("print(false && true);", "print(false);")
    assertOptimized(a + "print(false && a);", a + "print(false);")
    assertOptimized(a + "print(1 == 2 && a);", a + "print(false);")

    assertOptimized("print(true && true);", "print(true);")
    assertOptimized(a + "print(true && a);", a + "print(true && a);")

    assertOptimized(a + "print(1 != 1 && a);", a + "print(false);")
    assertOptimized(a + "print(1 / 0 == 1 && a);", a + "print(1 / 0 == 1 && a);")

    assertOptimized("print(true || false);", "print(true);")
    assertOptimized(a + "print(true || a);", a + "print(true);")
    assertOptimized(a + "print(1 == 1 || a);", a + "print(true);")

    assertOptimized("print(false || false);", "print(false);")
    assertOptimized(a + "print(false || a);", a + "print(false || a);")

    assertOptimized(a + "print(1 == 1 || a);", a + "print(true);")
    assertOptimized(a + "print(1 / 0 == 0 || a);", a + "print(1 / 0 == 0 || a);")
  }

  @Test
  def divisionByZero() {
    assertOptimized("print(1 / 0);", "print(1 / 0);")
  }


  protected def assertOptimized(before: String, after: String) {
    val clean = before.filterNot(_ == '\r')
    val root = ProgramParser.parse(ToyLexer.analyze(clean))
    assertNoProblemsIn(root.elements)
    val document = new DocumentImpl(clean)
    Optimizer.optimize(root, document)
    Assert.assertEquals(after, document.text)
  }
}