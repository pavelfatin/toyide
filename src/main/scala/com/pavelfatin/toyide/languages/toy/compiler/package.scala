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

package com.pavelfatin.toyide.languages.toy

import com.pavelfatin.toyide.compiler.TranslationException
import com.pavelfatin.toyide.languages.toy.node.{Parameter, Program, Scope, VariableDeclaration}
import com.pavelfatin.toyide.node.NodeType
import com.pavelfatin.toyide.Extensions._

package object compiler {
  implicit class RichToyType(nodeType: NodeType) {
    def descriptor: String = nodeType match {
      case ToyType.StringType => "Ljava/lang/String;"
      case ToyType.IntegerType => "I"
      case ToyType.BooleanType => "Z"
      case ToyType.VoidType => "V"
    }

    def prefix: Char = nodeType match {
      case ToyType.StringType => 'a'
      case ToyType.IntegerType |  ToyType.BooleanType => 'i'
      case ToyType.VoidType =>throw new TranslationException("No prefix for void type")
    }
  }

  implicit class RichVariableDeclaration(variable: VariableDeclaration) {
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

  implicit class RichParameter(parameter: Parameter) {
    def ordinal: Int = parameter.previousSiblings.filterBy[Parameter].size
  }
}
