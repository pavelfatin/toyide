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
import com.pavelfatin.toyide.interpreter.{DelegateValue, Place}
import com.pavelfatin.toyide.languages.lisp.LispType
import com.pavelfatin.toyide.languages.lisp.core.CoreFunction

class SymbolValue private (val content: String, val place: Option[Place]) extends DelegateValue[String] with Expression {
  def valueType = LispType.SymbolType

  def eval(environment: Environment, output: Output) = {
    CoreFunction.NameToFunction.get(content).orElse(environment.lookup(content))
      .getOrElse(environment.interrupt("Undefined symbol: " + content, place))
  }
}

object SymbolValue {
  def apply(name: String, place: Option[Place] = None): SymbolValue = new SymbolValue(name, place)

  def unapply(value: SymbolValue): Some[String] = Some(value.content)
}