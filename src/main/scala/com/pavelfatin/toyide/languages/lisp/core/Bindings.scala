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
import com.pavelfatin.toyide.languages.lisp.parameters.Parameters
import com.pavelfatin.toyide.languages.lisp.value.{Environment, Expression}

trait Bindings { self: CoreFunction =>
  protected def bind(elements: Seq[Expression], environment: Environment, output: Output): Environment = {
    if (elements.size % 2 > 0) error("an even number of expressions is required", environment)

    val pairs = elements.grouped(2).toSeq.map { case Seq(pattern, initializer) =>
      val parameters = Parameters.from(pattern).fold(error(_, environment), identity)
      (parameters, initializer)
    }

    pairs.foldLeft(environment) { case (env, (parameters, initializer)) =>
      val bindings = parameters.bind(initializer.eval(env, output)).fold(error(_, env), identity)
      env.addLocals(bindings)
    }
  }
}
