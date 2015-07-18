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