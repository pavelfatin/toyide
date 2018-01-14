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