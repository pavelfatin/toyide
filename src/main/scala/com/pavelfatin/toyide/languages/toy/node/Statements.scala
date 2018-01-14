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

package com.pavelfatin.toyide.languages.toy.node

import com.pavelfatin.toyide.node._
import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.languages.toy.interpreter._
import com.pavelfatin.toyide.languages.toy.compiler._

class Assignment extends NodeImpl("assignment")
with ExpressionHolder with AssignmentEvaluator with AssignmentTranslator {
  def reference: Option[ReferenceNode] = children.findBy[ReferenceNode]

  def expression = children.dropWhile(!_.token.exists(_.kind == EQ)).findBy[Expression]

  def expectedType = reference.flatMap(_.target).collect {
    case TypedNode(nodeType) => nodeType
  }
}

class Return extends NodeImpl("return") with ExpressionHolder with ReturnEvaluator with ReturnTranslator {
  def expression = children.findBy[Expression]

  def expectedType = parents.findBy[FunctionDeclaration].flatMap(_.nodeType)
}

object Return {
  def unapply(r: Return) = Some((r.expression, r.expectedType))
}

class While extends NodeImpl("while") with BlockHolder with ConditionHolder with WhileEvaluator with WhileTranslator {
  def expression = children.findBy[Expression]
}

class If extends NodeImpl("if") with BlockHolder with ConditionHolder with IfEvaluator with IfTranslator {
  def expression = children.findBy[Expression]

  def elseBlock = children.filterBy[Block].lift(1)
}

object If {
  def unapply(node: If) = Some((node.expression, node.block, node.elseBlock))
}

class Call extends NodeImpl("call") with CallEvaluator with CallTranslator {
  def expression = children.findBy[CallExpression]
}

class Comment extends NodeImpl("comment")

class Empty extends NodeImpl("empty")
