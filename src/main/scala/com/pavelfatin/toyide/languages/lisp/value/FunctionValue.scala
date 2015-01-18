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
import com.pavelfatin.toyide.languages.lisp.LispType

trait FunctionValue extends Expression {
  def name: Option[String]

  def valueType = LispType.FunctionType

  def eval(environment: Environment, output: Output) =
    environment.interrupt("Evaluation of " + presentation)

  def isLazy: Boolean

  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression

  protected def error(message: String, environment: Environment): Nothing =
    environment.interrupt(presentation + ": " + message)
}
