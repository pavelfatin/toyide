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

object Gt extends CoreFunction(">") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(IntegerValue(i1), IntegerValue(i2)) => BooleanValue(i1 > i2)
    case _ => expected("i1 i2", arguments, environment)
  }
}

object GtEq extends CoreFunction(">=") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(IntegerValue(i1), IntegerValue(i2)) => BooleanValue(i1 >= i2)
    case _ => expected("i1 i2", arguments, environment)
  }
}

object Lt extends CoreFunction("<") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(IntegerValue(i1), IntegerValue(i2)) => BooleanValue(i1 < i2)
    case _ => expected("i1 i2", arguments, environment)
  }
}

object LtEq extends CoreFunction("<=") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(IntegerValue(i1), IntegerValue(i2)) => BooleanValue(i1 <= i2)
    case _ => expected("i1 i2", arguments, environment)
  }
}

object Eq extends CoreFunction("=") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) =
    BooleanValue(apply0(arguments, environment, output))

  private def apply0(arguments: Seq[Expression], environment: Environment, output: Output): Boolean = {
    arguments match {
      case Seq(IntegerValue(i1), IntegerValue(i2)) => i1 == i2
      case Seq(CharacterValue(c1), CharacterValue(c2)) => c1 == c2
      case Seq(BooleanValue(b1), BooleanValue(b2)) => b1 == b2
      case Seq(ListValue(l1), ListValue(l2)) =>
        l1.size == l2.size && l1.zip(l2).map(p => Seq(p._1, p._2)).forall(apply0(_, environment, output))
      case _ => expected("v1 v2", arguments, environment)
    }
  }
}
