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

import com.pavelfatin.toyide.languages.toy.node._
import com.pavelfatin.toyide.interpreter._
import com.pavelfatin.toyide.Output

trait AssignmentEvaluator extends ExpressionHolderEvaluator { self: Assignment =>
  override def evaluate(context: Context, output: Output) = {
    val ref = reference.getOrElse(
      interrupt(context, "Reference for assignment not found %s", span.text))

    val target = ref.target.getOrElse(
      interrupt(context, "Target for reference not found %s", ref.span.text))

    def local = target match {
      case v: VariableDeclaration => v.local
      case p: Parameter => true
      case _ => interrupt(context, "Non-value target for reference %s: %s", ref.identifier, target.span.text)
    }

    val newValue = evaluateExpression(context, output)

    wrap(context) {
      context.update(local, ref.identifier, newValue)
    }

    None
  }
}