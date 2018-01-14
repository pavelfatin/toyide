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
import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.languages.toy.interpreter._
import com.pavelfatin.toyide.languages.toy.compiler._

trait Scope extends Node with ScopeEvaluator with ScopeTranslator {
  def canRedefineOuterDeclarations = parent match {
    case Some(_: FunctionDeclaration) => true
    case _ => false
  }

  def functions: Seq[FunctionDeclaration] =
    children.filterBy[FunctionDeclaration]

  def variables: Seq[VariableDeclaration] =
    children.filterBy[VariableDeclaration]

  def parameters: Seq[Parameter] = parent match {
    case Some(function: FunctionDeclaration) => function.parameters
    case _ => Seq.empty
  }

  def values: Seq[UsableNode] = parameters ++ variables

  def declarations: Seq[UsableNode] = functions ++ values

  def exit: Option[Node] = children.find {
    case _: Return => true
    case If(_, Some(block), Some(elseBlock)) if block.exit.isDefined && elseBlock.exit.isDefined => true
    case _ => false
  }
}

object ScopeDeclarations {
  def unapply(scope: Scope) = Some(scope.declarations)
}

object ScopeExit {
  def unapply(scope: Scope) = scope.exit
}
