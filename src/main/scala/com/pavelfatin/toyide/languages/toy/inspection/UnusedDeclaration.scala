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

package com.pavelfatin.toyide.languages.toy.inspection

import com.pavelfatin.toyide.languages.toy.node._
import com.pavelfatin.toyide.inspection.{Decoration, Mark, Inspection}
import com.pavelfatin.toyide.node.{IdentifiedNode, Node}

object UnusedDeclaration extends Inspection {
  val Message = (entity: String, name: String) =>
    "%s '%s' is never used".format(entity.capitalize, name)

  def inspect(node: Node): Seq[Mark] = node match {
    case ScopeDeclarations(declarations) =>
      val unused = for (declaration <- declarations;
                        elements = declaration.elements
                        if declaration.usages.forall(elements.contains)) yield declaration
      unused.collect {
        case node @ IdentifiedNode(id, identifier) =>
          val entity = node match {
            case _: FunctionDeclaration => "function"
            case _: VariableDeclaration => "variable"
            case _ => "parameter"
          }
          Mark(id, Message(entity, identifier), Decoration.Dim, warning = true)
      }
    case _ => Seq.empty
  }
}