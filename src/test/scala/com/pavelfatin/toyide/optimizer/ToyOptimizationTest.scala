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