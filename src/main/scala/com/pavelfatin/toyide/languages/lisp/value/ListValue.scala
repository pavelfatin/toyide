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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.interpreter.{DelegateValue, Place}
import com.pavelfatin.toyide.languages.lisp.LispType

class ListValue private (val content: List[Expression], val place: Option[Place]) extends DelegateValue[List[Expression]] with Expression {
  def valueType = LispType.ListType

  private val macroExpansionCache = new SingleValueCache[(MacroFunction, Seq[Expression]), Expression]()

  def eval(environment: Environment, output: Output) = {
    if (content.nonEmpty) content.head.eval(environment, output) match {
      case m: MacroFunction =>
        val arguments = content.tail
        val expansion = macroExpansionCache.getOrUpdate((m, arguments)) {
          m.expand(arguments, environment.inFrame(place), output)
        }
        expansion.eval(environment, output)
      case f: FunctionValue =>
        val expressions = content.tail
        val arguments = if (f.isLazy) expressions else expressions.map(_.eval(environment, output))
        f.apply(arguments, environment.inFrame(place), output)
      case v => environment.interrupt("Cannot apply to " + v.presentation, place)
    } else {
      environment.interrupt("Empty application", place)
    }
  }

  override def presentation = content.map(_.presentation).mkString("(", " ", ")")
}

object ListValue {
  val Empty = ListValue(Nil)

  def apply(expressions: Seq[Expression], place: Option[Place] = None): ListValue =
    new ListValue(expressions.toList, place)

  def unapply(list: ListValue): Option[List[Expression]] = Some(list.content)
}
