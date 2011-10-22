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

import com.pavelfatin.toyide.languages.toy.node.{Parameter, VariableDeclaration, ReferenceToValue}
import com.pavelfatin.toyide.compiler.{Code, Labels}

trait ReferenceToValueTranslator extends ToyTranslatable { self: ReferenceToValue =>
  override def translate(name: String, labels: Labels) = {
    val node = target.getOrElse(
      interrupt("Target value not found: %s", identifier))

    val referenceType = nodeType.getOrElse(
      interrupt("Unknown target value type: %s", identifier))

    val s = node match {
      case v: VariableDeclaration =>
        if (v.global) {
          "aload_0\ngetfield %s/%s %s\n".format(name, identifier, referenceType.descriptor)
        } else {
          "%cload %d\n".format(referenceType.prefix, v.ordinal + 1)
        }
      case p: Parameter =>
        "%cload %d\n".format(referenceType.prefix, p.ordinal + 1)
      case _ =>
        interrupt("Non-value target for reference %s: %s", identifier, node.span.text)
    }

    Code(withLine(s))
  }
}