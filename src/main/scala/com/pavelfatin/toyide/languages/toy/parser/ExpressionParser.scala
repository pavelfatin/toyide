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