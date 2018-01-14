/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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