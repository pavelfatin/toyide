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

import com.pavelfatin.toyide.languages.toy.node._
import com.pavelfatin.toyide.interpreter._
import com.pavelfatin.toyide.Output

trait ReferenceToValueEvaluator extends ToyEvaluable { self: ReferenceToValue =>
  override def evaluate(context: Context, output: Output): Option[Value] = {
    val node = target.getOrElse(
      interrupt(context, "Target value not found: %s", identifier))

    def local = node match {
      case v: VariableDeclaration => v.local
      case p: Parameter => true
      case _ => interrupt(context, "Non-value target for reference %s: %s", identifier, node.span.text)
    }

    wrap[Some[Value]](context) { // [T] bug in Scalac ?
      Some(context.get(local, identifier))
    }
  }
}