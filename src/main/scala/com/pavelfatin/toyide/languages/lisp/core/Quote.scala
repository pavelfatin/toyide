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

object Quote extends CoreFunction("quote", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(expression) => expression
    case _ => expected("expression", arguments, environment)
  }

  def apply(value: Expression): Expression = ListValue(Seq(SymbolValue("quote"), value))
}

object Unquote extends CoreFunction("unquote", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(expression) => error("not applicable outside syntax quoting", environment)
    case _ => expected("expression", arguments, environment)
  }

  def apply(value: Expression): Expression = ListValue(Seq(SymbolValue("unquote"), value))

  def unapply(expression: Expression): Option[Expression] = expression match {
    case ListValue(Seq(SymbolValue("unquote"), value)) => Some(value)
    case _ => None
  }
}

object UnquoteSplicing extends CoreFunction("unquote-splicing", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(expression) => error("not applicable outside syntax quoting", environment)
    case _ => expected("expression", arguments, environment)
  }

  def apply(value: Expression): Expression = ListValue(Seq(SymbolValue("unquote-splicing"), value))

  def unapply(expression: Expression): Option[Expression] = expression match {
    case ListValue(Seq(SymbolValue("unquote-splicing"), value)) => Some(value)
    case _ => None
  }
}

object GenSym extends CoreFunction("gensym") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(StringValue(prefix)) => SymbolValue(prefix + "_" + environment.nextId())
    case _ => expected("prefix", arguments, environment)
  }
}

object Quasiquote extends CoreFunction("quasiquote", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(expression) =>
      val names = new NameGenerator(environment)
      def quasiquote(expression: Expression): Expression = expression match {
        case Unquote(value) => value.eval(environment, output)
        case UnquoteSplicing(value) => error("splicing outside of list", environment)
        case list @ ListValue(values) =>
          val splicedValues = values.flatMap {
            case UnquoteSplicing(value) => value.eval(environment, output) match {
              case ListValue(children) => children
              case v => error("splicing of non-list value: " + v.presentation, environment)
            }
            case value => Seq(quasiquote(value))
          }
          ListValue(splicedValues, list.place)
        case SymbolValue(s) if s.endsWith("#") =>
          val prefix = s.substring(0, s.length - 1)
          SymbolValue(names.unique(prefix))
        case value => value
      }
      quasiquote(expression)
    case _ => expected("expression", arguments, environment)
  }

  def apply(value: Expression): Expression = ListValue(Seq(SymbolValue("quasiquote"), value))
}
