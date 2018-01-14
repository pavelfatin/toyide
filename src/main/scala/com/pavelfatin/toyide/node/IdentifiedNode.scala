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

trait IdentifiedNode extends Node {
  def id: Option[Node]

  def identifier: String = id.map(_.span.text).mkString

  override def toString = "%s(%s)".format(kind, identifier)
}

object IdentifiedNode {
  def unapply(node: IdentifiedNode) =
    node.id.map((_, node.identifier))
}

object IdentifiedNodeId {
  def unapply(node: IdentifiedNode) =
    node.id
}