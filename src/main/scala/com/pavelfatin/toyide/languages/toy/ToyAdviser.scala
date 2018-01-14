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

import com.pavelfatin.toyide.editor.{Variant, Adviser}
import com.pavelfatin.toyide.Extensions._
import node._
import com.pavelfatin.toyide.node.{Expression, Node}

object ToyAdviser extends Adviser {
  private val PredefinedFunctions = List("print", "println").map(asFunction)

  private val DefinitionKeywords = List("def", "var").map(asKeyword)

  private val ControlKeywords = List("if", "while").map(asControl)

  private val ElseKeyword = List("else").map(asKeyword)

  private val ReturnKeyword = List("return").map(asLiteral)

  private val TypeKeywords = List("string", "integer", "boolean").map(asLiteral)

  private val FunctionTypeKeywords = List("void", "string", "integer", "boolean").map(asKeyword)

  private val BooleanLiterals = List("true", "false").map(asLiteral)

  def variants(root: Node, anchor: Node): Seq[Variant] = {
    val holders = anchor.parents.filterNot(_.isLeaf)

    lazy val elseKeyword = anchor.previousSibling match {
      case Some(statement: If) if statement.elseBlock.isEmpty => ElseKeyword
      case _ => Seq.empty
    }

    holders.headOption match {
      case Some(spec: TypeSpec) =>
        spec.parent match {
          case Some(_: FunctionDeclaration) => FunctionTypeKeywords
          case _ => TypeKeywords
        }
      case Some(_: Expression) =>
        referencesFor(anchor) ++ BooleanLiterals
      case Some(_: Program) =>
        referencesFor(anchor) ++ PredefinedFunctions ++ DefinitionKeywords ++ elseKeyword ++ ControlKeywords
      case Some(_: Block) =>
        val returnKeyword = holders.findBy[FunctionDeclaration].toSeq.flatMap(_ => ReturnKeyword)
        referencesFor(anchor) ++ PredefinedFunctions ++ elseKeyword ++ returnKeyword ++ ControlKeywords
      case _ => Seq.empty
    }
  }

  private def referencesFor(node: Node): Seq[Variant] = {
    val declarations = node.parents
      .filterBy[Scope]
      .flatMap(_.declarations)
      .filter(_.span.begin < node.span.begin)
    
    val parameters = declarations.filterBy[Parameter].map(p => asValue(p.identifier))
    val functions = declarations.filterBy[FunctionDeclaration].map(f => asFunction(f.identifier))
    val variables = declarations.filterBy[VariableDeclaration].filterNot(node.parents.contains).map(v => asValue(v.identifier))

    (parameters.sortBy(_.title) ++ functions.sortBy(_.title) ++ variables.sortBy(_.title)).distinct
  }

  private def asFunction(name: String) = Variant(name, name.formatted("%s()"), -1)

  private def asValue(name: String) = Variant(name, name, 0)

  private def asLiteral(name: String) = Variant(name, name, 0)

  private def asKeyword(name: String) = Variant(name, name.formatted("%s "), 0)

  private def asControl(name: String) = Variant(name, name.formatted("%s ()"), -1)
}