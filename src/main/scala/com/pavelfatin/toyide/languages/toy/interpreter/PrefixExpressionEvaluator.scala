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