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

package com.pavelfatin.toyide.languages.toy.compiler

import com.pavelfatin.toyide.languages.toy.node.Literal
import com.pavelfatin.toyide.languages.toy.ToyType
import com.pavelfatin.toyide.compiler.{Code, Labels}

trait LiteralTranslator extends ToyTranslatable { self: Literal =>
  override def translate(name: String, labels: Labels) = {
    val s = span.text

    val content = nodeType match {
      case Some(ToyType.StringType) | Some(ToyType.IntegerType) => s
      case Some(ToyType.BooleanType) =>
        s match {
          case "true" => "1"
          case "false" => "0"
          case _ => interrupt("Incorrect literal: %s", s)
        }
      case _ => interrupt("Incorrect literal: %s", s)
    }

    Code(withLine("ldc %s\n".format(content)))
  }
}