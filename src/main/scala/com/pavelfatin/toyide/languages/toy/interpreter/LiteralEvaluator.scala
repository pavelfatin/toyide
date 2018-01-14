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
import com.pavelfatin.toyide.languages.toy.node.Literal
import com.pavelfatin.toyide.interpreter._
import com.pavelfatin.toyide.Output

trait LiteralEvaluator extends ToyEvaluable { self: Literal =>
  override def evaluate(context: Context, output: Output): Option[Value] = {
    val s = span.text

    val kind = tokenKind.getOrElse(
      interrupt(context, "Unable to determine token kind for literal", span.text))

    val value = kind match {
      case STRING_LITERAL => parseStringValue(s)
      case NUMBER_LITERAL => parseIntegerValue(s, context)
      case BOOLEAN_LITERAL => parseBooleanValue(s, context)
      case it => interrupt(context, "Unknown literal token: %s", it)
    }

    Some(value)
  }

  private def parseStringValue(s: String) = {
    val text = s.substring(1, s.length - 1)
    StringValue(text)
  }

  private def parseIntegerValue(s: String, context: Context) = {
    try {
      val i = Integer.parseInt(s)
      IntegerValue(i)
    } catch {
      case e: NumberFormatException => interrupt(context, "Wrong integer literal: %s", s)
    }
  }

  private def parseBooleanValue(s: String, context: Context) = {
    val b = s match {
      case "true" => true
      case "false" => false
      case _ => interrupt(context, "Wrong boolean literal: %s", s)
    }
    BooleanValue(b)
  }
}