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

import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.languages.toy.interpreter.ToyValue._
import com.pavelfatin.toyide.interpreter._
import com.pavelfatin.toyide.languages.toy.node.PrefixExpression
import com.pavelfatin.toyide.Output

trait PrefixExpressionEvaluator extends ToyEvaluable { self: PrefixExpression =>
  override def evaluate(context: Context, output: Output): Option[Value] = {
    val t = prefix.getOrElse(
      interrupt(context, "Prefix token not found: %s", span.text))

    val exp = expression.getOrElse(
      interrupt(context, "Inner expression not found: %s", span.text))

    val value = exp.evaluate(context, output)

    t.kind match {
      case BANG => value match {
        case Some(BooleanValue(v)) => Some(BooleanValue(!v))
        case _ => interrupt(context, "Wrong inner expression type: %s", exp.span.text)
      }
      case PLUS => value match {
        case Some(IntegerValue(v)) => Some(IntegerValue(v))
        case _ => interrupt(context, "Wrong inner expression type: %s", exp.span.text)
      }
      case MINUS => value match {
        case Some(IntegerValue(v)) => Some(IntegerValue(-v))
        case _ => interrupt(context, "Wrong inner expression type: %s", exp.span.text)
      }
      case _ => interrupt(context, "Incorrect prefix: %s", t.span.text)
    }
  }
}