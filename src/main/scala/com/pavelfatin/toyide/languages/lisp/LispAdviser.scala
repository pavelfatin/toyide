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

package com.pavelfatin.toyide.languages.lisp

import com.pavelfatin.toyide.editor.{Adviser, Variant}
import com.pavelfatin.toyide.languages.lisp.core.CoreFunction
import com.pavelfatin.toyide.languages.lisp.library.Library
import com.pavelfatin.toyide.languages.lisp.node.SymbolNode
import com.pavelfatin.toyide.node.Node

object LispAdviser extends Adviser {
  def variants(root: Node, anchor: Node): Seq[Variant] = {
    anchor.parent match {
      case Some(symbol: SymbolNode) =>
        val localSymbols = symbol.accessibleSymbols.map(_.identifier).filter(!_.endsWith(Adviser.Anchor))
        val symbols = localSymbols ++ CoreFunction.Names ++ Library.instance.symbols
        symbols.map(name => Variant(name, name, 0))
      case _ => Seq.empty
    }
  }
}