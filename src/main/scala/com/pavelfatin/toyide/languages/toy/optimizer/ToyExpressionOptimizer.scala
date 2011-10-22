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

package com.pavelfatin.toyide.languages.toy.optimizer

import com.pavelfatin.toyide.interpreter.{EvaluationException, ContextImpl}
import com.pavelfatin.toyide.languages.toy.node.ToyExpression
import com.pavelfatin.toyide.languages.toy.interpreter.ToyValue.StringValue

trait ToyExpressionOptimizer { self: ToyExpression =>
  override lazy val optimized: Option[String] = {
    if (self.constant) {
      try {
        self.evaluate(new ContextImpl(), NullOutput) collect {
          case v: StringValue => "\"%s\"".format(v.presentation)
          case v => v.presentation
        }
      } catch {
        case _: EvaluationException => None
      }
    } else {
      None
    }
  }
}