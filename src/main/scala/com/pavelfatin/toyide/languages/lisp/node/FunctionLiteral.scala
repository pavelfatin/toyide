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

import com.pavelfatin.toyide.languages.lisp.value.{Expression, ListValue, SymbolValue}

object FunctionLiteral {
  private val PositionalArgument = "%(\\d+)".r

  def readFrom(list: ListNode, source: String): ListValue = {
    val nodes = list.expressions

    val parameters = parametersFor(indicesIn(nodes))

    val expressions = expressionsFor(nodes, source)

    ListValue(Seq(SymbolValue("fn"), ListValue(parameters), ListValue(expressions, Some(list.placeIn(source)))))
  }

  private def indicesIn(nodes: Seq[ExpressionNode]): Seq[Int] = nodes.flatMap {
    case SymbolNode(symbol) => indexFor(symbol).toSeq
    case ListNode(children @ _*) => indicesIn(children)
    case _ => Seq.empty
  }

  private def indexFor(symbol: String): Option[Int] = Some(symbol) collect {
    case "%" => 1
    case "%&" => -1
    case PositionalArgument(digits) => digits.toInt
  }

  private def parametersFor(indices: Seq[Int]): Seq[Expression] = {
    if (indices.isEmpty) Seq.empty else
      Range(1, indices.max + 1).map(parameterFor) ++
        (if (indices.contains(-1)) Seq(SymbolValue("&"), parameterFor(-1)) else Seq.empty)
  }

  private def parameterFor(index: Int): Expression = {
    val name = if (index == -1) "_ps" else "_p" + index
    SymbolValue(name)
  }

  private def expressionsFor(nodes: Seq[ExpressionNode], source: String): Seq[Expression] = nodes.map {
    case node @ SymbolNode(symbol) =>
      indexFor(symbol).map(parameterFor).getOrElse(node.read(source))
    case list @ ListNode(children @ _*) =>
      ListValue(expressionsFor(children, source), Some(list.placeIn(source)))
    case node => node.read(source)
  }
}

