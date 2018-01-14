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