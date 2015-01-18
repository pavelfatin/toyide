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
import com.pavelfatin.toyide.languages.lisp.value.{Environment, Expression, IntegerValue}

abstract class ArithmeticFunction(name: String) extends CoreFunction(name) {
  protected def toIntegers(arguments: Seq[Expression], environment: Environment): Seq[Int] = arguments.map {
    case IntegerValue(i) => i
    case _ => expected("i1 i2 ...", arguments, environment)
  }
}

object Add extends ArithmeticFunction("+") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) =
    IntegerValue(toIntegers(arguments, environment).sum)
}

object Sub extends ArithmeticFunction("-") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(IntegerValue(i)) => IntegerValue(-i)
    case Seq(IntegerValue(head), tail @ _*) => IntegerValue(toIntegers(tail, environment).fold(head)(_ - _))
    case _ => expected("i1 in...", arguments, environment)
  }
}

object Mul extends ArithmeticFunction("*") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) =
    IntegerValue(toIntegers(arguments, environment).product)
}

object Div extends ArithmeticFunction("/") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = {
    val xs = toIntegers(arguments, environment)
    if (xs.length < 2) expected("i1 i2 ...", arguments, environment)
    if (xs.tail.contains(0)) error("Division by zero: " + xs.mkString(" "), environment)
    IntegerValue(xs.reduceLeft(_ / _))
  }
}

object Mod extends ArithmeticFunction("mod") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(IntegerValue(i1), IntegerValue(i2)) =>
      if (i2 == 0) error("Division by zero", environment) else IntegerValue(i1 % i2)
    case _ => expected("i1 i2", arguments, environment)
  }
}
