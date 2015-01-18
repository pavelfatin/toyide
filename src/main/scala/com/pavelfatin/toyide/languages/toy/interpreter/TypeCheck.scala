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

package com.pavelfatin.toyide.languages.toy.interpreter

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.interpreter.{Value, Context}
import com.pavelfatin.toyide.languages.toy.ToyType._
import com.pavelfatin.toyide.languages.toy.interpreter.ToyValue._
import com.pavelfatin.toyide.node.{Expression, NodeType}

trait TypeCheck extends ToyEvaluable with Expression {
  abstract override def evaluate(context: Context, output: Output) = {
    val result = super.evaluate(context, output)
    check(context, nodeType, result)
    result
  }

  private def check(context: Context, nodeType: Option[NodeType], value: Option[Value]) {
    nodeType match {
      case Some(t) =>
        (t, value) match {
          case (StringType, Some(_: StringValue)) =>
          case (IntegerType, Some(_: IntegerValue)) =>
          case (BooleanType, Some(_: BooleanValue)) =>
          case (VoidType, None) =>
          case _ => interrupt(context, "Type case exception, expected: %s, actual: %s", t, value)
        }
      case None => interrupt(context, "Evaluation of node with unknown type: %s", span.text)
    }
  }
}