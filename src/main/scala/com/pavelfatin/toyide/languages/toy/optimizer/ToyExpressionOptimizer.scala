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