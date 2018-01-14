/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
