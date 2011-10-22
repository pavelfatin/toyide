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
import com.pavelfatin.toyide.lexer.TokenKind
import com.pavelfatin.toyide.languages.toy.ToyType._
import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.languages.toy.interpreter._
import com.pavelfatin.toyide.languages.toy.compiler._

class Block extends NodeImpl("block") with Scope

class Program extends NodeImpl("program") with Scope

trait UsableNode extends IdentifiedNode {
  def scope = parents.findBy[Scope]

  def usages: Seq[ReferenceNode] = {
    scope match {
      case Some(it) => it.elements.filterBy[ReferenceNode].filter(_.isReferenceTo(this))
      case None => Seq.empty
    }
  }
}

class FunctionDeclaration extends NodeImpl("function") with BlockHolder
with UsableNode with NamedNode with TypedNode with FunctionDeclarationTranslator {
  def id = children.drop(1).headOption

  def parametersNode: Option[Parameters] = children.findBy[Parameters]

  def parameters: Seq[Parameter] = parametersNode.map(_.parameters).getOrElse(Seq.empty)

  def name = "%s(%s): %s".format(
    identifier, parameters.map(_.name).mkString(", "), nodeType.map(_.presentation).getOrElse("undefined"))

  def typeSpec: Option[TypeSpec] = children.findBy[TypeSpec]

  def typeId = typeSpec.flatMap(_.typeId)

  def nodeType = typeSpec.flatMap(_.declaredType)
}

object FunctionBlock {
  def unapply(function: FunctionDeclaration) = function.block
}

class VariableDeclaration extends NodeImpl("variable") with UsableNode with NamedNode
with TypedNode with ExpressionHolder with VariableDeclarationEvaluator with VariableDeclarationTranslator {
  def id = children.drop(1).headOption

  def name = "%s: %s".format(
    identifier, nodeType.map(_.presentation).getOrElse("undefined"))

  def typeSpec: Option[TypeSpec] = children.findBy[TypeSpec]

  def typeId = typeSpec.flatMap(_.typeId)

  def nodeType = typeSpec.flatMap(_.declaredType)

  def expression = children.dropWhile(!_.token.exists(_.kind == EQ)).findBy[Expression]

  def expectedType = nodeType

  def local = parents.findBy[FunctionDeclaration].isDefined
}

class TypeSpec extends NodeImpl("typeSpec") {
  def typeId: Option[Node] = children.lastOption

  def declaredType = typeId.flatMap(_.token).flatMap(token => from(token.kind))

  private def from(kind: TokenKind) = Some(kind).collect {
    case STRING => StringType
    case INTEGER => IntegerType
    case BOOLEAN => BooleanType
    case VOID => VoidType
  }
}

class Parameter extends NodeImpl("parameter") with UsableNode with NamedNode with TypedNode {
  def id = children.headOption

  override def scope = parent match {
    case Some(parameters: Parameters) => parents.findBy[FunctionDeclaration].flatMap(_.block)
    case _ => super.scope
  }

  def name = "%s: %s".format(identifier, typeSpec.flatMap(_.typeId).map(_.span.text).mkString)

  def typeSpec: Option[TypeSpec] = children.findBy[TypeSpec]

  def typeId = typeSpec.flatMap(_.typeId)

  def nodeType = typeSpec.flatMap(_.declaredType)
}

class Parameters extends NodeImpl("parameters") {
  def parameters: Seq[Parameter] = children.filterBy[Parameter]
}

class Arguments extends NodeImpl("arguments") {
  def expressions: Seq[Expression] = children.filterBy[Expression]
}
