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

import com.pavelfatin.toyide.languages.toy.ToyType
import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.languages.toy.node._
import com.pavelfatin.toyide.node._
import com.pavelfatin.toyide.compiler._

trait ToyTranslatable extends Translatable { self: Node =>
  protected def interrupt(message: String, values: Any*): Nothing =
    throw new TranslationException(message.format(values: _*))

  protected implicit def toRichToyType(nodeType: NodeType) = new {
    def descriptor: String = nodeType match {
      case ToyType.StringType => "Ljava/lang/String;"
      case ToyType.IntegerType => "I"
      case ToyType.BooleanType => "Z"
      case ToyType.VoidType => "V"
    }

    def prefix: Char = nodeType match {
      case ToyType.StringType => 'a'
      case ToyType.IntegerType |  ToyType.BooleanType => 'i'
      case ToyType.VoidType => interrupt("No prefix for void type")
    }
  }

  protected implicit def toRichVariableDeclaration(variable: VariableDeclaration) = new {
    def ordinal: Int = variable.parents
      .filterBy[Scope]
      .init
      .flatMap(_.values)
      .count(_.span.begin < variable.span.begin)

    def global: Boolean = variable.parent match {
      case Some(_: Program) => true
      case _ => false
    }
  }

  protected implicit def toRichParameter(parameter: Parameter) = new {
    def ordinal: Int = parameter.previousSiblings.filterBy[Parameter].size
  }

  protected def withLine(s: String): String = {
    val line = self.span.source.take(self.span.begin).count(_ == '\n')
    ".line %d\n%s".format(line, s)
  }
}