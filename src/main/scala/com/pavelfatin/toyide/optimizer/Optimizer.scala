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

package com.pavelfatin.toyide.optimizer

import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.node.Node

object Optimizer {
  def optimize(root: Node, document: Document) {
    optimizationsIn(root).reverse.foreach(p => document.replace(p._1.span.interval, p._2))
  }

  private def optimizationsIn(node: Node): Seq[(Node, String)] = node.optimized match {
    case Some(s) => if (node.span.text == s) Seq.empty else Seq(node -> s)
    case None => node.children.flatMap(optimizationsIn)
  }
}