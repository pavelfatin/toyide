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
