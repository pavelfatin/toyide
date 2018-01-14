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