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

import com.pavelfatin.toyide.languages.toy.node.CallExpression
import com.pavelfatin.toyide.compiler.{Labels, Code}

trait CallExpTranslator extends ToyTranslatable { self: CallExpression =>
  override def translate(name: String, labels: Labels) = {
    val ref = reference.getOrElse(
      interrupt("Function not found: %s", span.text))

    val s = if (ref.predefined) {
      translatePredefinedCall(name, labels)
    } else {
      translateCall(name, labels)
    }

    Code(withLine(s))
  }

  private def translatePredefinedCall(name: String, labels: Labels) = {
    val ref = reference.getOrElse(
      interrupt("Function not found: %s", span.text))

    val parts = expressions.map { exp =>
      val expType = exp.nodeType.getOrElse(
        interrupt("Unknow expression type: %s", exp.span.text))

      "aload_0\ngetfield %s/out Ljava/io/PrintStream;\n%sinvokevirtual java/io/PrintStream/print(%s)V\n".format(
        name, exp.translate(name, labels).instructions, expType.descriptor)
    }

    val tail = if (ref.identifier == "println")
      "aload_0\ngetfield %s/out Ljava/io/PrintStream;\nldc \"\\n\"\ninvokevirtual java/io/PrintStream/print(Ljava/lang/String;)V\n"
        .format(name)
    else
      ""

    parts.mkString + tail
  }

  private def translateCall(name: String, labels: Labels) = {
    val f = function.getOrElse(
      interrupt("Function not found: %s", span.text))

    val returnType = f.nodeType.getOrElse(
      interrupt("Unknown function return type: %s", span.text))

    val parameterTypes = f.parameters.map { it =>
      it.nodeType.getOrElse(
        interrupt("Unknown parameter type: %s", it.span.text))
    }

    val data = expressions.map(_.translate(name, labels).instructions).mkString

    "aload_0\n%s\ninvokevirtual %s/%s(%s)%s\n".format(
      data, name, f.identifier, parameterTypes.map(_.descriptor).mkString(""), returnType.descriptor)
  }
}