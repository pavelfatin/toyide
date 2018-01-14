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