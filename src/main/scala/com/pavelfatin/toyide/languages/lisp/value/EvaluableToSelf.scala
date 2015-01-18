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
import com.pavelfatin.toyide.interpreter.DelegateValue
import com.pavelfatin.toyide.languages.lisp.LispType

trait EvaluableToSelf extends Expression {
  def eval(environment: Environment, output: Output): Expression = this
}

case class BooleanValue(content: Boolean) extends DelegateValue[Boolean] with EvaluableToSelf {
  def valueType = LispType.BooleanType
}

case class IntegerValue(content: Int) extends DelegateValue[Int] with EvaluableToSelf {
  def valueType = LispType.IntegerType
}

case class CharacterValue(content: Char) extends DelegateValue[Char] with EvaluableToSelf {
  def valueType = LispType.CharacterType

  override def presentation = {
    val s = content match {
      case ' ' => "space"
      case '\t' => "tab"
      case '\r' => "return"
      case '\n' => "newline"
      case c => c
    }
    "\\" + s
  }
}
