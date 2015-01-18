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