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
import com.pavelfatin.toyide.lexer.Token
import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.languages.toy.ToyType._
import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.languages.toy.interpreter._
import com.pavelfatin.toyide.languages.toy.compiler._
import com.pavelfatin.toyide.languages.toy.optimizer.ToyExpressionOptimizer

trait ToyExpression extends Expression with ToyExpressionOptimizer

class Literal extends NodeImpl("literal")
with ToyExpression with LiteralEvaluator with TypeCheck with LiteralTranslator {
  protected def tokenKind = children.headOption.flatMap(_.token).map(_.kind)

  override def constant = true

  lazy val nodeType = tokenKind.collect {
    case STRING_LITERAL => StringType
    case NUMBER_LITERAL => IntegerType
    case BOOLEAN_LITERAL => BooleanType
  }

  override def toString = "%s(%s)".format(kind, span.text)
}

class PrefixExpression extends NodeImpl("prefixExpression")
with ToyExpression with PrefixExpressionEvaluator with TypeCheck with PrefixExpressionTranslator {
  def prefix: Option[Token] = children.headOption.flatMap(_.token)

  def expression: Option[Expression] = children.findBy[Expression]

  override def constant = expression.map(_.constant).getOrElse(false)

  lazy val nodeType = {
    prefix.map(_.kind).zip(expression.flatMap(_.nodeType)).headOption collect {
      case (BANG, BooleanType) => BooleanType
      case (PLUS | MINUS, IntegerType) => IntegerType
    }
  }
}

object PrefixExpression {
  def unapply(node: PrefixExpression) = Some(node.prefix, node.expression)
}

class BinaryExpression extends NodeImpl("binaryExpression")
with ToyExpression with BinaryExpressionEvaluator with TypeCheck with BinaryExpressionTranslator {
  def parts: Option[(Expression, Token, Expression)] = children match {
    case (left: Expression) :: NodeToken(token) :: (right: Expression) :: Nil => Some(left, token, right)
    case _ => None
  }

  override def constant = children match {
    case (l: Expression) :: _ ::  (r: Expression) :: Nil if l.constant && r.constant => true
    case (l @ Expression(BooleanType)) :: NodeToken(Token(kind, _, _)) ::  (r @ Expression(BooleanType)) :: Nil =>
      kind match {
        case AMP_AMP => l.optimized == Some("false")
        case BAR_BAR => l.optimized == Some("true")
        case _ => false
      }
    case _ => false
  }

  private def signature = parts collect {
    case (Expression(leftType), token, Expression(rightType)) => (leftType, token.kind, rightType)
  }

  lazy val nodeType = signature.collect {
    case (BooleanType, AMP_AMP, BooleanType) => BooleanType

    case (BooleanType, BAR_BAR, BooleanType) => BooleanType

    case (IntegerType, GT, IntegerType) => BooleanType
    case (IntegerType, GT_EQ, IntegerType) => BooleanType
    case (IntegerType, LT, IntegerType) => BooleanType
    case (IntegerType, LT_EQ, IntegerType) => BooleanType

    case (StringType, EQ_EQ, StringType) => BooleanType
    case (IntegerType, EQ_EQ, IntegerType) => BooleanType
    case (BooleanType, EQ_EQ, BooleanType) => BooleanType

    case (StringType, BANG_EQ, StringType) => BooleanType
    case (IntegerType, BANG_EQ, IntegerType) => BooleanType
    case (BooleanType, BANG_EQ, BooleanType) => BooleanType

    case (IntegerType, STAR, IntegerType) => IntegerType
    case (IntegerType, SLASH, IntegerType) => IntegerType
    case (IntegerType, PERCENT, IntegerType) => IntegerType

    case (IntegerType, PLUS, IntegerType) => IntegerType
    case (IntegerType, MINUS, IntegerType) => IntegerType
    case (StringType, PLUS, _) => StringType
  }
}

object BinaryExpression {
  def unapply(exp: BinaryExpression) = exp.parts
}

class CallExpression extends NodeImpl("callExpression")
with ToyExpression with CallExpEvaluator with TypeCheck with CallExpTranslator {
  def reference: Option[ReferenceToFunction] = children.findBy[ReferenceToFunction]

  def function: Option[FunctionDeclaration] =
    reference.flatMap(_.target).map(_.asInstanceOf[FunctionDeclaration])

  def arguments: Option[Arguments] = children.findBy[Arguments]

  def expressions: Seq[Expression] = arguments.map(_.expressions).getOrElse(Seq.empty)

  def bindings: (Seq[(Expression, Parameter)], Seq[Expression], Seq[Parameter]) = {
    val parameters = function.map(_.parameters).getOrElse(Seq.empty)
    val es = expressions.iterator
    val ps = parameters.iterator
    (es.zip(ps).toList, es.toSeq, ps.toSeq)
  }

  def rightBrace: Option[Node] = arguments.flatMap(_.children.lastOption)

  lazy val nodeType = reference.flatMap { it =>
    if (it.predefined) Some(VoidType) else function.flatMap(_.nodeType)
  }
}

class Group extends NodeImpl("group")
with ToyExpression with GroupEvaluator with TypeCheck with GroupTranslator {
  def child = children.findBy[Expression]

  override def constant = child.map(_.constant).getOrElse(false)

  lazy val nodeType = child.flatMap(_.nodeType)
}