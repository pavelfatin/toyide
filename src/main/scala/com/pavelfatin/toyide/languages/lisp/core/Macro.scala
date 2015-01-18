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

object Eval extends CoreFunction("eval") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(expression) => expression.eval(environment, output)
    case _ => expected("expression", arguments, environment)
  }
}

object Macro extends CoreFunction("macro", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = {
    def createMacro(name: Option[String], parameterList: ListValue, expressions: Seq[Expression]) = {
      val parameters = Parameters.from(parameterList).fold(error(_, environment), identity)
      new MacroFunction(name, parameters, expressions, environment.locals)
    }

    arguments match {
      case Seq(parameterList: ListValue, expressions @ _*) =>
        createMacro(None, parameterList, expressions)
      case Seq(SymbolValue(name), parameterList: ListValue, expressions @ _*) =>
        createMacro(Some(name), parameterList, expressions)
      case _ => expected("[params*] exprs*", arguments, environment)
    }
  }
}

object Macroexpand extends CoreFunction("macroexpand") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(ListValue(Seq(head, tail @ _*))) => head.eval(environment, output) match {
      case m: MacroFunction => m.expand(tail, environment, output)
      case _ => error("macro application expected", environment)
    }
    case _ => expected("list", arguments, environment)
  }
}
