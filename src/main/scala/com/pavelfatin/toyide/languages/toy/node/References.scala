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
