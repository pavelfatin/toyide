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

import com.pavelfatin.toyide.languages.toy.node._
import com.pavelfatin.toyide.inspection.{Mark, Inspection}
import com.pavelfatin.toyide.node.Node
import com.pavelfatin.toyide.languages.toy.ToyType

object VoidValue extends Inspection {
  val Message = "Value type cannot be void"

  def inspect(node: Node): Seq[Mark] = node match {
    case p: Parameter if p.nodeType.contains(ToyType.VoidType) =>
      p.typeId.map(Mark(_, Message)).toSeq
    case v: VariableDeclaration if v.nodeType.contains(ToyType.VoidType) =>
      v.typeId.map(Mark(_, Message)).toSeq
    case _ => Seq.empty
  }
}