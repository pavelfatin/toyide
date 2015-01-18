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

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.interpreter._

trait Expression extends Value {
  def eval(environment: Environment, output: Output): Expression

  override def toString = "%s: %s".format(presentation, valueType.presentation)
}

object Expression {
  def format(expressions: Seq[Expression]): String =
    expressions.map(format).mkString(" ")

  private def format(expression: Expression): String = expression match {
    case StringValue(s) => s
    case value => value.presentation
  }
}