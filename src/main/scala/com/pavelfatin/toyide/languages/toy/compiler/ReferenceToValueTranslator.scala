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

import com.pavelfatin.toyide.languages.toy.node.{Parameter, VariableDeclaration, ReferenceToValue}
import com.pavelfatin.toyide.compiler.{Code, Labels}

trait ReferenceToValueTranslator extends ToyTranslatable { self: ReferenceToValue =>
  override def translate(name: String, labels: Labels) = {
    val node = target.getOrElse(
      interrupt("Target value not found: %s", identifier))

    val referenceType = nodeType.getOrElse(
      interrupt("Unknown target value type: %s", identifier))

    val s = node match {
      case v: VariableDeclaration =>
        if (v.global) {
          "aload_0\ngetfield %s/%s %s\n".format(name, identifier, referenceType.descriptor)
        } else {
          "%cload %d\n".format(referenceType.prefix, v.ordinal + 1)
        }
      case p: Parameter =>
        "%cload %d\n".format(referenceType.prefix, p.ordinal + 1)
      case _ =>
        interrupt("Non-value target for reference %s: %s", identifier, node.span.text)
    }

    Code(withLine(s))
  }
}