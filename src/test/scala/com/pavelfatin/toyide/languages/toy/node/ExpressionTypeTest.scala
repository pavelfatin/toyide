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