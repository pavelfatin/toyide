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

import com.pavelfatin.toyide.node.Node
import com.pavelfatin.toyide.languages.toy.node.FunctionDeclaration
import com.pavelfatin.toyide.interpreter.{Place, Context, EvaluationException}
import com.pavelfatin.toyide.Extensions._

trait ToyEvaluable { self: Node =>
  protected def interrupt(context: Context, message: String, values: Any*) =
    throw new EvaluationException(message.format(values: _*), place :: context.trace.toList)

  protected def wrap[T](context: => Context)(action: => T): T = {
    try {
      action
    } catch {
      case e: IllegalStateException => interrupt(context, e.getMessage)
    }
  }

  protected def place = {
    val enclosure = self.parents.findBy[FunctionDeclaration].map(_.identifier)
    val line = self.span.source.take(self.span.begin).count(_ == '\n')
    Place(enclosure, line)
  }
}