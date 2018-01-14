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