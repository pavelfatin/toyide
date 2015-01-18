/*
 * Copyright (C) 2014 Pavel Fatin <http://pavelfatin.com>
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

package com.pavelfatin.toyide.languages.lisp.node

import com.pavelfatin.toyide.languages.lisp.core.CoreFunction
import com.pavelfatin.toyide.languages.lisp.value.SymbolValue
import com.pavelfatin.toyide.node._

class SymbolNode extends NodeImpl("symbol")
  with ExpressionNode with ReferenceNode with IdentifiedNode with TargetResolution {

  def read0(source: String) = SymbolValue(text, Some(placeIn(source)))

  def id = source

  def source = if (quoted) children.drop(1).headOption else children.headOption

  def target = if (!resolvable) None else accessibleSymbols.find {
    case SymbolNode(name) => name == identifier
    case _ => false
  }

  override def identifier = source.map(_.span.text).mkString

  def predefined = CoreFunction.Names.contains(identifier)

  def resolvable: Boolean = !(predefined || declaration || title)

  private def declaration: Boolean = parents.exists { parent =>
    localSymbolsIn(parent).contains(this) || globalSymbolsIn(parent).contains(this)
  }

  private def title: Boolean = parent.exists {
    case ListNode(SymbolNode("fn" | "macro"), name: SymbolNode, etc @ _*) => name == this
    case _ => false
  }

  override def toString = "%s(%s)".format(kind, identifier)
}

object SymbolNode {
  def unapply(node: SymbolNode) = Some(node.identifier)
}
