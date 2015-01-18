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

import com.pavelfatin.toyide.node.Node

trait TargetResolution { self: ExpressionNode =>
  def accessibleSymbols: Seq[SymbolNode] =
    parents.flatMap(localSymbolsIn).toSeq ++ parents.last.children.flatMap(globalSymbolsIn).toSeq

  protected def localSymbolsIn(node: Node): Set[SymbolNode] = node match {
    case ListNode(SymbolNode("fn" | "macro"), ListNode(ps @ _*), etc @ _*) => symbolsInPatterns(ps)
    case ListNode(SymbolNode("loop" | "let" | "if-let"), ListNode(bs @ _*), etc @ _*) => symbolsInBindings(bs)
    case ListNode(SymbolNode("fn" | "macro" | "defn" | "defmacro"), SymbolNode(_), ListNode(ps @ _*), etc @ _*) => symbolsInPatterns(ps)
    case _ => Set.empty
  }

  protected def globalSymbolsIn(node: Node): Set[SymbolNode] = node match {
    case ListNode(SymbolNode("def" | "defn" | "defmacro"), symbol: SymbolNode, etc @ _*) => Set(symbol)
    case _ => Set.empty
  }

  private def symbolsInPatterns(patterns: Seq[ExpressionNode]): Set[SymbolNode] = {
    val symbols = patterns.flatMap(_.elements).collect {
      case symbol: SymbolNode => symbol
    }
    symbols.toSet
  }

  private def symbolsInBindings(bindings: Seq[ExpressionNode]): Set[SymbolNode] = {
    val patterns = bindings.grouped(2).map(_.head).toSeq
    symbolsInPatterns(patterns)
  }
}
