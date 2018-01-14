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