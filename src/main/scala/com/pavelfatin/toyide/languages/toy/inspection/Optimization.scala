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

package com.pavelfatin.toyide.languages.toy.inspection

import com.pavelfatin.toyide.node.Node
import com.pavelfatin.toyide.inspection.{Decoration, Mark, Inspection}

object Optimization extends Inspection {
  val Message = "Can be simplified to '%s' (use Code / Optimize)".format(_: String)

  def inspect(node: Node): Seq[Mark] = {
    node.parent match {
      case Some(p) if p.optimized.isDefined => Seq.empty
      case _ => node.optimized match {
        case Some(s) if s != node.span.text => Seq(Mark(node, Message(s), Decoration.Fill, true))
        case _ => Seq.empty
      }
    }
  }
}