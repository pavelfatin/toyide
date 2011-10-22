/*
 * Copyright (C) 2011 Pavel Fatin <http://pavelfatin.com>
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

package com.pavelfatin.toyide.languages.toy.inspection

import com.pavelfatin.toyide.node._
import com.pavelfatin.toyide.languages.toy.node._
import com.pavelfatin.toyide.inspection.{Mark, Inspection}
import com.pavelfatin.toyide.languages.toy.ToyType

object TypeMismatch extends Inspection {
  val Message = "Type mismatch, expected: %s, actual: %s".format(_: String, _: String)

  val ReturnFromVoidFunctionMessage = "Cannot return a value from a function with void return type"

  val MissingReturnValueMessage = "Missing return value"

  // for some reason compiler complains when the following is written in a single "match"
  def inspect(node: Node): Seq[Mark] = node match {
    case r @ Return(None, Some(expected)) if expected != ToyType.VoidType =>
      Seq(Mark(r, MissingReturnValueMessage))
    case r @ Return(Some(_), Some(expected)) if expected == ToyType.VoidType =>
      Seq(Mark(r, ReturnFromVoidFunctionMessage))
    case _ => node match {
      case ExpressionHolder(Some(exp @ Expression(actual)), Some(expected)) if expected != actual =>
        Seq(Mark(exp, Message(expected.presentation, actual.presentation)))
      case _ => Seq.empty
    }
  }
}