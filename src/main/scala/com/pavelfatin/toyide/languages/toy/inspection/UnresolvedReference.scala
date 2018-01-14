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

import com.pavelfatin.toyide.node._
import com.pavelfatin.toyide.languages.toy.node._
import com.pavelfatin.toyide.inspection.{Decoration, Mark, Inspection}

object UnresolvedReference extends Inspection {
  val Message = "Cannot resolve %s '%s'".format(_: String, _: String)

  def inspect(node: Node): Seq[Mark] = node match {
    case ref @ ReferenceNode(Some(source), None) if !ref.predefined =>
      node match {
        case _: ReferenceToFunction => Seq(Mark(node, Message("function", source.span.text), Decoration.Red))
        case _: ReferenceToValue => Seq(Mark(node, Message("value", source.span.text), Decoration.Red))
      }
    case _ => Seq.empty
  }
}