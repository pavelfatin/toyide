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
import com.pavelfatin.toyide.interpreter.Place
import com.pavelfatin.toyide.node.Node

trait ExpressionNode extends Node with ReadableNode with QuotableNode {
  def placeIn(source: String): Place = {
    val line = span.source.take(span.begin).count(_ == '\n')
    Place(Some(enclosure.map(source + "." + _).getOrElse(source)), line)
  }

  private def enclosure: Option[String] = {
    val enclosures = parents.flatMap {
      case list: ListNode => list.expressions match {
        case Seq(SymbolNode("fn" | "macro" | "defn" | "defmacro"), SymbolNode(name), etc @ _ *) => Seq(name)
        case _ => Seq.empty
      }
      case _ => Seq.empty
    }
    enclosures.headOption
  }
}