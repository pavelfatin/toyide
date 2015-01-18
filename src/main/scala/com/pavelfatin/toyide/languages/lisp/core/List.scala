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

object NewList extends CoreFunction("list") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = ListValue(arguments)

  def apply(values: Seq[Expression]): Expression = ListValue(SymbolValue("list") +: values)
}

object Cons extends CoreFunction("cons") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(x, ListValue(l)) => ListValue(x :: l)
    case _ => expected("x list", arguments, environment)
  }
}

object First extends CoreFunction("first") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(ListValue(l)) =>
      if (l.isEmpty) error("first on empty list", environment) else l.head
    case _ => expected("list", arguments, environment)
  }
}

object Rest extends CoreFunction("rest") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(ListValue(l)) =>
      if (l.isEmpty) error("rest on empty list", environment) else ListValue(l.tail)
    case _ => expected("list", arguments, environment)
  }
}

