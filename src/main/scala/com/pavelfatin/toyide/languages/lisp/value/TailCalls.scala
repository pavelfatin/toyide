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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.languages.lisp.parameters.Parameters

import scala.annotation.tailrec

trait TailCalls { self: FunctionValue =>
  protected def withTailCalls(parameters: Parameters, initialEnvironment: Environment)
                             (evaluateIn: Environment => Expression): Expression = {
    @tailrec
    def evaluateIterativelyIn(environment: Environment): Expression = evaluateIn(environment) match {
      case RecurValue(arguments) =>
        val bindings = parameters.bind(ListValue(arguments)).fold(error(_, environment), identity)
        evaluateIterativelyIn(environment.addLocals(bindings))
      case expr => expr
    }

    evaluateIterativelyIn(initialEnvironment)
  }
}
