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

import com.pavelfatin.toyide.languages.toy.node.VariableDeclaration
import com.pavelfatin.toyide.compiler.{Labels, Code}

trait VariableDeclarationTranslator extends ToyTranslatable { self: VariableDeclaration =>
  override def translate(name: String, labels: Labels) = {
    val variableType = nodeType.getOrElse(
      interrupt("Unknown variable type: %s", span.text))

    val exp = expression.getOrElse(
      interrupt("Initializer expression not found: %s", span.text))

    val expCode = exp.translate(name, labels).instructions

    if (self.global) {
      val field = ".field private %s %s\n".format(identifier, variableType.descriptor)
      val initializer = "aload_0\n%sputfield %s/%s %s\n".format(expCode, name, identifier, variableType.descriptor)
      Code(withLine(initializer), field)
    } else {
      Code(withLine("%s%cstore %d\n".format(expCode, variableType.prefix, self.ordinal + 1)))
    }
  }
}