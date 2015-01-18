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

package com.pavelfatin.toyide.languages.lisp.core

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.languages.lisp.value._

object And extends CoreFunction("and", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = {
    val isFalse = arguments.toIterator.map(_.eval(environment, output)).exists {
      case BooleanValue(b) => !b
      case _ => expected("b1 b2 ...", arguments, environment)
    }
    BooleanValue(!isFalse)
  }
}

object Or extends CoreFunction("or", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = {
    val isTrue = arguments.toIterator.map(_.eval(environment, output)).exists {
      case BooleanValue(b) => b
      case _ => expected("b1 b2 ...", arguments, environment)
    }
    BooleanValue(isTrue)
  }
}

object Not extends CoreFunction("not") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(BooleanValue(b)) => BooleanValue(!b)
    case _ => expected("b", arguments, environment)
  }
}
