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

import com.pavelfatin.toyide.interpreter._
import com.pavelfatin.toyide.languages.toy.node.ExpressionHolder
import com.pavelfatin.toyide.node.Node
import com.pavelfatin.toyide.Output

trait ExpressionHolderEvaluator extends ToyEvaluable { self: ExpressionHolder with Node =>
  protected def evaluateExpression(context: Context, output: Output): Value = {
    val exp = expression.getOrElse(
      interrupt(context, "Expression not found: %s", span.text))

    val value = exp.evaluate(context, output).getOrElse(
      interrupt(context, "Expression return no value: %s", exp.span.text))

    expectedType.foreach { expected =>
      val actual = value.valueType
      if (actual != expected) {
        interrupt(context, "Type mismatch, expected: %s, actual: %s", expected.presentation, actual.presentation)
      }
    }

    value
  }
}