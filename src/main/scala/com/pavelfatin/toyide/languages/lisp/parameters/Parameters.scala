/*
 * Copyright (C) 2014 Pavel Fatin <http://pavelfatin.com>
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

package com.pavelfatin.toyide.languages.lisp.parameters

import com.pavelfatin.toyide.languages.lisp.value.{Expression, ListValue, SymbolValue}

import scala.util.control.Exception

trait Parameters {
  def bind(argument: Expression): Either[String, Map[String, Expression]]

  def presentation: String

  def symbols: Seq[String]
}

object Parameters {
  private val Separator =  "&"

  def from(expression: Expression): Either[String, Parameters] =
    Exception.catching(classOf[MalformedParametersException])
      .either(from0(expression)).left.map(_.getMessage)

  private def from0(expression: Expression): Parameters = {
    val parameters = toParameters(expression)

    val duplicates = duplicatesIn(parameters.symbols)

    if (duplicates.nonEmpty)
      error("Duplicate parameters: " + duplicates.mkString(" "))

    parameters
  }

  private def toParameters(expression: Expression): Parameters = expression match {
    case SymbolValue(name) => new NamedParameter(name)

    case ListValue(children) =>
      val (singularExpressions, pluralExpression) = split(children)

      val singularParameters = singularExpressions.map(toParameters)
      val pluralParameters = pluralExpression.map(toParameters)

      new ParameterList(singularParameters, pluralParameters)

    case e => error("Non-symbol in parameter list: " + e)
  }

  private def split(expressions: Seq[Expression]): (Seq[Expression], Option[Expression]) = {
    val (singularExpressions, restExpressions) = expressions.span(!isSeparator(_))

    val pluralExpressions = restExpressions.drop(1)

    if (pluralExpressions.exists(isSeparator) || pluralExpressions.size > 1)
      error("Single variable-length parameter expected, but was: " + pluralExpressions.mkString(" "))

    (singularExpressions, pluralExpressions.headOption)
  }

  private val isSeparator: Expression => Boolean = {
    case SymbolValue(Separator) => true
    case _ => false
  }

  private def duplicatesIn[T](seq: Seq[T]): Seq[T] =
    seq.groupBy(identity).filter(_._2.size > 1).keys.toSeq

  private def error(message: String) = throw new MalformedParametersException(message)

  private class MalformedParametersException(message: String) extends Exception(message)
}
