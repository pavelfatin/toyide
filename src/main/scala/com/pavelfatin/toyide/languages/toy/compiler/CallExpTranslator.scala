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