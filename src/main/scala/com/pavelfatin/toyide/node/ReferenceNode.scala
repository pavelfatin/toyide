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

package com.pavelfatin.toyide.node

trait ReferenceNode extends Node {
  def source: Option[Node]

  def identifier = source.map(_.span.text).mkString

  def target: Option[Node]

  def isReferenceTo(node: Node) = target match {
    case Some(it) => it == node
    case None => false
  }

  def predefined: Boolean

  def unresolved = !predefined && target.isEmpty

  override def toString = "%s(%s)".format(kind, identifier)
}

object ReferenceNode {
  def unapply(reference: ReferenceNode) =
    Some((reference.source, reference.target))
}

object ReferenceNodeTarget {
  def unapply(reference: ReferenceNode) = reference.target
}