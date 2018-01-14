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
import com.pavelfatin.toyide.languages.toy.node.CallExpression
import com.pavelfatin.toyide.Output

trait CallExpEvaluator extends ToyEvaluable { self: CallExpression =>
  override def evaluate(context: Context, output: Output) = {
    val ref = reference.getOrElse(
      interrupt(context, "Function not found: %s", span.text))

    if (ref.predefined) {
      evaluatePredefinedCall(context, output)
    } else {
      evaluateCall(context, output)
    }
  }

  private def evaluatePredefinedCall(context: Context, output: Output): Option[Value] = {
    for (exp <- expressions; value <- exp.evaluate(context, output))
      output.print(value.presentation)

    for (ref <- reference; id = ref.identifier if id == "println")
      output.print("\n")

    None
  }

  private def evaluateCall(context: Context, output: Output): Option[Value] = {
    val (pairs, unboundExpressions, unboundParameters) = bindings

    if (unboundExpressions.nonEmpty)
      interrupt(context, "Too many arguments: %s", span.text)

    if (unboundParameters.nonEmpty)
      interrupt(context, "Not enough arguments: %s", span.text)

    val arguments = for ((exp, parameter) <- pairs) yield {
      val value = exp.evaluate(context, output).getOrElse(
        interrupt(context, "Argument yield no value: %s", exp.span.text))

      val parameterType = parameter.nodeType.getOrElse(
        interrupt(context, "Unknown parameter type: %s", parameter.span.text))

      if (parameterType != value.valueType)
        interrupt(context, "Type mismatch, expected: %s, actual: %s",
          parameterType.presentation, value.valueType.presentation)

      (parameter.identifier, value)
    }

    val f = function.getOrElse(
      interrupt(context, "Function not found: %s", span.text))

    val block = f.block.getOrElse(
      interrupt(context, "Function block not found: %s", f.span.text))

    wrap(context) {
      context.inFrame(place) {
        context.inScope {
          for ((name, value) <- arguments)
            context.put(true, name, value)

          block.evaluate(context, output)
        }
      }
    }
  }
}