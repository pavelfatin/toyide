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

package com.pavelfatin.toyide.languages.toy.inspection

import com.pavelfatin.toyide.languages.toy.ToyType._
import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.languages.toy.node.BinaryExpression
import com.pavelfatin.toyide.node.{NodeToken, Expression, Node}
import com.pavelfatin.toyide.lexer.Token
import com.pavelfatin.toyide.inspection.{Decoration, Mark, Inspection}

object DivisionByZero extends Inspection {
  val Message = "Division by zero"

  def inspect(node: Node): Seq[Mark] = node match {
    case e: BinaryExpression => e.children match {
        case (Expression(IntegerType)) :: NodeToken(Token(SLASH, _, _)) ::  (r @ Expression(IntegerType)) :: Nil
          if r.optimized.contains("0") => Seq(Mark(e, Message, Decoration.Fill, true))
        case _ => Seq.empty
      }
    case _ => Seq.empty
  }
}