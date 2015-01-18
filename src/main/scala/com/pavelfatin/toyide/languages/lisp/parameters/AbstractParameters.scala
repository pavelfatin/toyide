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

package com.pavelfatin.toyide.languages.lisp.parameters

import com.pavelfatin.toyide.languages.lisp.value.Expression

import scala.util.control.Exception._

trait AbstractParameters extends Parameters {
  final def bind(argument: Expression) =
    catching(classOf[BindingException])
      .either(bind0(argument)).left.map(_.getMessage)

  protected def bind0(argument: Expression): Map[String, Expression]

  protected def error(message: String) = throw new BindingException(message)

  private class BindingException(message: String) extends Exception(message)
}
