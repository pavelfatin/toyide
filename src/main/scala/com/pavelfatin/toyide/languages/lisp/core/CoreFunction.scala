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

import com.pavelfatin.toyide.languages.lisp.value._

abstract class CoreFunction(val name0: String, val isLazy: Boolean = false) extends FunctionValue {
  def name = Some(name0)

  def presentation = "core." + name0

  protected def expected(parameters: String, arguments: Seq[Expression], environment: Environment): Nothing =
    environment.interrupt("%s syntax: (%s %s), application: (%s %s)"
      .format(presentation, name0, parameters, name0, Expression.format(arguments)))
}

object CoreFunction {
  private val Functions: Set[CoreFunction] = Set(
    IsInteger, IsBoolean, IsCharacter, IsSymbol, IsFunction, IsList,
    Quote, Quasiquote, Unquote, GenSym,
    Eval, Macro, Macroexpand,
    Def, Let, Fn, Apply,
    Do, If, Error, Loop, Recur,
    Print, PrintLn, Trace, Format,
    Dir, Exists, Directory, Open, Listen, Read, Write, Flush, Close,
    NewList, Cons, First, Rest,
    Add, Sub, Mul, Div, Mod,
    Gt, GtEq, Lt, LtEq, Eq,
    And, Or, Not)

  val NameToFunction: Map[String, CoreFunction] = Functions.map(f => (f.name0, f)).toMap

  val Names: Set[String] = NameToFunction.keySet
}
