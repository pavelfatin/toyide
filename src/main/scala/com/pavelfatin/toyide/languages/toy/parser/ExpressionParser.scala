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

package com.pavelfatin.toyide.languages.toy.parser

import com.pavelfatin.toyide.parser.{TreeBuilder, Parser}
import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.languages.toy.node._

object ExpressionParser extends Parser {
  def parse(in: TreeBuilder) {
    logicalOr()

    def logicalOr() {
      in.folding(new BinaryExpression(), true) {
        logicalAnd()
        while (in.grasp(BAR_BAR)) logicalAnd()
      }
    }

    def logicalAnd() {
      in.folding(new BinaryExpression(), true) {
        equality()
        while (in.grasp(AMP_AMP)) equality()
      }
    }

    def equality() {
      in.folding(new BinaryExpression(), true) {
        relation()
        while (in.grasp(EQ_EQ, BANG_EQ)) relation()
      }
    }

    def relation() {
      in.folding(new BinaryExpression(), true) {
        addition()
        while (in.grasp(LT, LT_EQ, GT_EQ, GT)) addition()
      }
    }

    def addition() {
      in.folding(new BinaryExpression(), true) {
        multiplication()
        while (in.grasp(PLUS, MINUS)) multiplication()
      }
    }

    def multiplication() {
      in.folding(new BinaryExpression(), true) {
        prefix()
        while (in.grasp(STAR, SLASH, PERCENT)) prefix()
      }
    }

    def prefix() {
      if (in.matches(PLUS, MINUS, BANG)) {
        in.capturing(new PrefixExpression()) {
          in.consume()
          prefix()
        }
      } else {
        atom()
      }
    }

    def atom() {
      if (in.matches(LPAREN)) {
        in.capturing(new Group()) {
          in.consume()
          logicalOr()
          in.consume(RPAREN)
        }
      } else {
        if(in.matches(IDENT)) {
          if(in.ahead(LPAREN))
            CallExpressionParser.parse(in)
          else in.capturing(new ReferenceToValue()) {
            in.consume(IDENT)
          }
        } else {
          if(in.matches(NUMBER_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL)) {
            in.capturing(new Literal()) {
              in.consume()
            }
          } else {
            in.error("Expression expected")
          }
        }
      }
    }
  }
}