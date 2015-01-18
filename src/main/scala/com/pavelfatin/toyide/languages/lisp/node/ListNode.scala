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

import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.languages.lisp.LispTokens._
import com.pavelfatin.toyide.languages.lisp.value.ListValue
import com.pavelfatin.toyide.node.NodeImpl

class ListNode extends NodeImpl("list") with ExpressionNode {
  protected def read0(source: String) = prefixKind match {
    case Some(HASH) => FunctionLiteral.readFrom(this, source)
    case _ => ListValue(expressions.map(_.read(source)), Some(placeIn(source)))
  }

  def expressions: Seq[ExpressionNode] = children.filterBy[ExpressionNode]

  def function: Option[ExpressionNode] = expressions.headOption

  def arguments: Seq[ExpressionNode] = expressions.drop(1)
}

object ListNode {
  def unapplySeq(node: ListNode) = Some(node.expressions)
}
