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