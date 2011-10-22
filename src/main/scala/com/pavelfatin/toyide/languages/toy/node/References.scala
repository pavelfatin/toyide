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

package com.pavelfatin.toyide.languages.toy.node

import com.pavelfatin.toyide.node._
import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.languages.toy.interpreter._
import com.pavelfatin.toyide.languages.toy.compiler._

trait ToyReference extends ReferenceNode {
  def source = children.headOption

  protected def targetIn(filter: Scope => Seq[IdentifiedNode]) = source.flatMap { node =>
    parents.filterBy[Scope].flatMap(filter)
      .filter(_.span.begin < node.span.begin)
      .find(it => it.identifier == identifier)
  }
}

class ReferenceToFunction extends NodeImpl("referenceToFunction") with ToyReference {
  private val PredefinedIdentifiers = List("print", "println")

  lazy val target = targetIn(_.functions)

  def predefined = PredefinedIdentifiers.contains(identifier)
}

class ReferenceToValue extends NodeImpl("referenceToValue") with ToyReference
with ToyExpression with ReferenceToValueEvaluator with TypeCheck with ReferenceToValueTranslator {
  lazy val target = targetIn(_.values)

  lazy val nodeType = target.collect {
    case TypedNode(t) => t
  }

  def predefined = false
}
