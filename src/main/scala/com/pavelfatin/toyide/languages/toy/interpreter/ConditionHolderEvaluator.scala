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

import com.pavelfatin.toyide.languages.toy.node.ConditionHolder
import com.pavelfatin.toyide.languages.toy.interpreter.ToyValue._
import com.pavelfatin.toyide.interpreter._
import com.pavelfatin.toyide.node.Node
import com.pavelfatin.toyide.Output

trait ConditionHolderEvaluator extends ExpressionHolderEvaluator { self: ConditionHolder with Node =>
  protected def evaluateCondition(context: Context, output: Output): Boolean = {
    evaluateExpression(context, output) match {
      case BooleanValue(b) => b
      case ValueType(t) => interrupt(context, "Wrong type (%s) for condition: %s", t.presentation, span.text)
    }
  }
}