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

package com.pavelfatin.toyide.languages.toy.compiler

import com.pavelfatin.toyide.languages.toy.node.Return
import com.pavelfatin.toyide.compiler.{Code, Labels}

trait ReturnTranslator extends ToyTranslatable { self: Return =>
  override def translate(name: String, labels: Labels) = {
    val returnCode = expression.map { exp =>
      val t = exp.nodeType.getOrElse(
        interrupt("Unknown return expression type: %s", span.text))

      "%s\n%creturn\n".format(exp.translate(name, labels).instructions, t.prefix)
    }
    Code(withLine(returnCode.getOrElse("return\n")))
  }
}