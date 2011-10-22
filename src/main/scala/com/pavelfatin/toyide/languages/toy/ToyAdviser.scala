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