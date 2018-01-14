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

import com.pavelfatin.toyide.languages.toy.node.FunctionDeclaration
import com.pavelfatin.toyide.compiler.{Labels, Code}

trait FunctionDeclarationTranslator extends ToyTranslatable { self: FunctionDeclaration =>
  private val Template = """
.method private %s(%s)%s
   .limit stack 10
   .limit locals 10

   %s

   return
.end method
"""

  override def translate(name: String, labels: Labels) = {
    val b = block.getOrElse(
      interrupt("Function block not found: %s", span.text))

    val returnType = nodeType.getOrElse(
      interrupt("Unknown function return type: %s", span.text))

    val parameterTypes = parameters.map { it =>
      it.nodeType.getOrElse(
        interrupt("Unknown parameter type: %s", it.span.text))
    }

    val s = Template.format(identifier,
      parameterTypes.map(_.descriptor).mkString(""),
      returnType.descriptor,
      b.translate(name, new Labels()).instructions)

    Code(methods = s)
  }
}