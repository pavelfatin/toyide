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
import com.pavelfatin.toyide.languages.lisp.value._

object Print extends CoreFunction("print") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = {
    output.print(Expression.format(arguments))
    ListValue.Empty
  }
}

object PrintLn extends CoreFunction("println") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = {
    output.print(Expression.format(arguments) + "\n")
    ListValue.Empty
  }
}

object Trace extends CoreFunction("trace") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(v) => output.print(v.presentation); v
    case _ => expected("value", arguments, environment)
  }
}

object Format extends CoreFunction("format") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = {
    ListValue(Expression.format(arguments).map(CharacterValue))
  }
}
