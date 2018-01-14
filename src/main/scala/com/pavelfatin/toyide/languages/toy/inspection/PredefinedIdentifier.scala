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

object PredefinedIdentifier extends Inspection {
  val Message = "Function name '%s' is predefined".format(_: String)

  def inspect(node: Node): Seq[Mark] = node match {
    case f: FunctionDeclaration if List("print", "println").contains(f.identifier) =>
      f.id.map(Mark(_, Message(f.identifier))).toSeq
    case _ => Seq.empty
  }
}