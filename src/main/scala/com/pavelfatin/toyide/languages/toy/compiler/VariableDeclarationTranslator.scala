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

import com.pavelfatin.toyide.languages.toy.node.VariableDeclaration
import com.pavelfatin.toyide.compiler.{Labels, Code}

trait VariableDeclarationTranslator extends ToyTranslatable { self: VariableDeclaration =>
  override def translate(name: String, labels: Labels) = {
    val variableType = nodeType.getOrElse(
      interrupt("Unknown variable type: %s", span.text))

    val exp = expression.getOrElse(
      interrupt("Initializer expression not found: %s", span.text))

    val expCode = exp.translate(name, labels).instructions

    if (self.global) {
      val field = ".field private %s %s\n".format(identifier, variableType.descriptor)
      val initializer = "aload_0\n%sputfield %s/%s %s\n".format(expCode, name, identifier, variableType.descriptor)
      Code(withLine(initializer), field)
    } else {
      Code(withLine("%s%cstore %d\n".format(expCode, variableType.prefix, self.ordinal + 1)))
    }
  }
}