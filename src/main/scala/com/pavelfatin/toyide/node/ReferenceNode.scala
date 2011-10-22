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