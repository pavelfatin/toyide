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

import com.pavelfatin.toyide.languages.toy.node.Literal
import com.pavelfatin.toyide.languages.toy.ToyType
import com.pavelfatin.toyide.compiler.{Code, Labels}

trait LiteralTranslator extends ToyTranslatable { self: Literal =>
  override def translate(name: String, labels: Labels) = {
    val s = span.text

    val content = nodeType match {
      case Some(ToyType.StringType) | Some(ToyType.IntegerType) => s
      case Some(ToyType.BooleanType) =>
        s match {
          case "true" => "1"
          case "false" => "0"
          case _ => interrupt("Incorrect literal: %s", s)
        }
      case _ => interrupt("Incorrect literal: %s", s)
    }

    Code(withLine("ldc %s\n".format(content)))
  }
}