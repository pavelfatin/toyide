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