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

import com.pavelfatin.toyide.node._
import com.pavelfatin.toyide.languages.toy.node._
import com.pavelfatin.toyide.inspection.{Mark, Inspection}
import com.pavelfatin.toyide.Extensions._

object DuplicateIdentifier extends Inspection {
  val FunctionRedefinition = "Function %s is already defined in the scope".format(_: String)

  val VariableRedefinition = "Variable %s is already defined in the scope".format(_: String)

  val ParameterRedefinition = "Parameter %s is already defined".format(_: String)

  def inspect(node: Node): Seq[Mark] = node match {
    case scope: Scope =>
      val functions = clashableIn(scope, _.functions)
      val variables = clashableIn(scope, _.variables)
      val parameters = clashableIn(scope, _.parameters)

      val functionClashes = clashesIn(functions).collect {
        case IdentifiedNode(id, identifier) => Mark(id, FunctionRedefinition(identifier))
      }
      val variableClashes = clashesIn(variables).collect {
        case IdentifiedNode(id, identifier) => Mark(id, VariableRedefinition(identifier))
      }
      val parameterClashes = clashesIn(parameters).collect {
        case IdentifiedNode(id, identifier) => Mark(id, ParameterRedefinition(identifier))
      }

      val parameterAndVariableClashes =
        (clashesIn(parameters ++ variables) diff clashesIn(parameters) diff clashesIn(variables)).collect {
          case IdentifiedNode(id, identifier) => Mark(id, ParameterRedefinition(identifier))
        }

      functionClashes ++ parameterClashes ++ parameterAndVariableClashes ++ variableClashes
    case _ => Seq.empty
  }

  private def clashableIn(scope: Scope, extractor: Scope => Seq[IdentifiedNode]): Seq[IdentifiedNode] = {
    val inner = extractor(scope)
    if (scope.canRedefineOuterDeclarations) {
      inner
    } else {
      val outer = scope.parents.findBy[Scope]
        .map(clashableIn(_, extractor)).getOrElse(Seq.empty)
        .filter(_.span.begin < scope.span.begin)
      outer ++ inner
    }
  }

  private def clashesIn(nodes: Seq[IdentifiedNode]): Seq[IdentifiedNode] = {
    nodes.groupBy(_.identifier).filter(_._2.size > 1).toSeq.flatMap(_._2.tail)
  }
}