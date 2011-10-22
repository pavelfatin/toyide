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

package com.pavelfatin.toyide.languages.toy.format

import com.pavelfatin.toyide.languages.toy.parser._
import org.junit.Test

class ExpressionFormatTest extends FormatTestBase {
  @Test
  def group() {
    assertFormatted("(1)", ExpressionParser, "(1)")
    assertFormatted("( 1)", ExpressionParser, "(1)")
    assertFormatted("(1 )", ExpressionParser, "(1)")
    assertFormatted("(  1  )", ExpressionParser, "(1)")
  }

  @Test
  def logicalOr() {
    assertFormatted("1||2", ExpressionParser, "1 || 2")
    assertFormatted("1 || 2", ExpressionParser, "1 || 2")
    assertFormatted("1  ||  2", ExpressionParser, "1 || 2")
    assertFormatted("1||2||3", ExpressionParser, "1 || 2 || 3")
  }

  @Test
  def logicalAnd() {
    assertFormatted("1&&2", ExpressionParser, "1 && 2")
    assertFormatted("1 && 2", ExpressionParser, "1 && 2")
    assertFormatted("1  &&  2", ExpressionParser, "1 && 2")
    assertFormatted("1&&2&&3", ExpressionParser, "1 && 2 && 3")
  }

  @Test
  def equality() {
    assertFormatted("1==2", ExpressionParser, "1 == 2")
    assertFormatted("1 == 2", ExpressionParser, "1 == 2")
    assertFormatted("1  ==  2", ExpressionParser, "1 == 2")
    assertFormatted("1==2==3", ExpressionParser, "1 == 2 == 3")
    assertFormatted("1!=2", ExpressionParser, "1 != 2")
    assertFormatted("1 != 2", ExpressionParser, "1 != 2")
    assertFormatted("1  !=  2", ExpressionParser, "1 != 2")
    assertFormatted("1!=2!=3", ExpressionParser, "1 != 2 != 3")
  }

  @Test
  def relation() {
    assertFormatted("1>2", ExpressionParser, "1 > 2")
    assertFormatted("1 > 2", ExpressionParser, "1 > 2")
    assertFormatted("1  >  2", ExpressionParser, "1 > 2")
    assertFormatted("1>2>3", ExpressionParser, "1 > 2 > 3")
    assertFormatted("1<2", ExpressionParser, "1 < 2")
    assertFormatted("1 < 2", ExpressionParser, "1 < 2")
    assertFormatted("1  <  2", ExpressionParser, "1 < 2")
    assertFormatted("1<2<3", ExpressionParser, "1 < 2 < 3")

    assertFormatted("1>=2", ExpressionParser, "1 >= 2")
    assertFormatted("1<=2", ExpressionParser, "1 <= 2")
  }

  @Test
  def addition() {
    assertFormatted("1+2", ExpressionParser, "1 + 2")
    assertFormatted("1 + 2", ExpressionParser, "1 + 2")
    assertFormatted("1  +  2", ExpressionParser, "1 + 2")
    assertFormatted("1+2+3", ExpressionParser, "1 + 2 + 3")
    assertFormatted("1-2", ExpressionParser, "1 - 2")
    assertFormatted("1 - 2", ExpressionParser, "1 - 2")
    assertFormatted("1  -  2", ExpressionParser, "1 - 2")
    assertFormatted("1-2-3", ExpressionParser, "1 - 2 - 3")
  }

  @Test
  def multiplication() {
    assertFormatted("1*2", ExpressionParser, "1 * 2")
    assertFormatted("1 * 2", ExpressionParser, "1 * 2")
    assertFormatted("1  *  2", ExpressionParser, "1 * 2")
    assertFormatted("1*2*3", ExpressionParser, "1 * 2 * 3")
    assertFormatted("1/2", ExpressionParser, "1 / 2")
    assertFormatted("1 / 2", ExpressionParser, "1 / 2")
    assertFormatted("1  /  2", ExpressionParser, "1 / 2")
    assertFormatted("1/2/3", ExpressionParser, "1 / 2 / 3")
  }

  @Test
  def modulus() {
    assertFormatted("1%2", ExpressionParser, "1 % 2")
    assertFormatted("1 % 2", ExpressionParser, "1 % 2")
    assertFormatted("1  %  2", ExpressionParser, "1 % 2")
  }

  // TODO Can we obey these rules with token-based formatter?
//  @Test
//  def prefix() {
//    assertFormatted("-1", ExpressionParser, "-1")
//    assertFormatted("- 1", ExpressionParser, "-1")
//    assertFormatted("--1", ExpressionParser, "--1")
//    assertFormatted("- - 1", ExpressionParser, "--1")
//    assertFormatted("++1", ExpressionParser, "++1")
//    assertFormatted("+ + 1", ExpressionParser, "+ + 1")
//    assertFormatted("+1", ExpressionParser, "+1")
//    assertFormatted("+ 1", ExpressionParser, "+1")
//  }

  @Test
  def expression() {
    assertFormatted("1+2*((3/4-2)>=5)<0||true&&6!=7", ExpressionParser,
      "1 + 2 * ((3 / 4 - 2) >= 5) < 0 || true && 6 != 7")

    assertFormatted("1  +  2  *  (  (  3  /  4  -  2  )  >=  5  )  <  0  ||  true  &&  6  !=  7", ExpressionParser,
      "1 + 2 * ((3 / 4 - 2) >= 5) < 0 || true && 6 != 7")
  }
}