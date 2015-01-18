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

package com.pavelfatin.toyide.languages.lisp.core

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.languages.lisp.parameters.Parameters
import com.pavelfatin.toyide.languages.lisp.value._

object Do extends CoreFunction("do") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) =
    arguments.lastOption.getOrElse(ListValue.Empty)
}

object If extends CoreFunction("if", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = {
    arguments match {
      case Seq(condition, left) =>
        if (isTruthy(condition.eval(environment, output))) left.eval(environment, output)
        else ListValue.Empty
      case Seq(condition, left, right) =>
        if (isTruthy(condition.eval(environment, output))) left.eval(environment, output)
        else right.eval(environment, output)
      case _ => expected("b exp1 [exp2]", arguments, environment)
    }
  }

  private def isTruthy(expression: Expression) = expression match {
    case BooleanValue(b) => b
    case ListValue(l) => l.nonEmpty
    case _ => true
  }
}

object Error extends CoreFunction("error") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) =
    environment.interrupt(Expression.format(arguments))
}

object Loop extends CoreFunction("loop", isLazy = true) with Bindings with TailCalls {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(ListValue(elements), expressions @ _*) =>
      val parameterList = ListValue(elements.grouped(2).toSeq.map(_.head))

      val parameters = Parameters.from(parameterList).fold(error(_, environment), identity)

      withTailCalls(parameters, bind(elements, environment, output)) { env =>
        expressions.map(_.eval(env, output)).lastOption.getOrElse(ListValue.Empty)
      }
    case _ => expected("[bindings*] exprs*", arguments, environment)
  }
}

object Recur extends CoreFunction("recur") with Bindings {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) =
    RecurValue(arguments)
}