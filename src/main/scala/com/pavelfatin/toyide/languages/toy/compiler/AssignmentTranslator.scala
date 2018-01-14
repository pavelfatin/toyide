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

import com.pavelfatin.toyide.languages.toy.node._
import com.pavelfatin.toyide.compiler.{Code, Labels}

trait AssignmentTranslator extends ToyTranslatable { self: Assignment =>
  override def translate(name: String, labels: Labels) = {
    val ref = reference match {
      case Some(it: ReferenceToValue) => it
      case Some(_) => interrupt("Incorrect target for assignment %s", span.text)
      case None => interrupt("Reference for assignment not found %s", span.text)
    }

    val target = ref.target.getOrElse(
      interrupt("Target for reference not found %s", ref.span.text))

    val referenceType = ref.nodeType.getOrElse(
      interrupt("Unknown target value type: %s", ref))

    val exp = expression.getOrElse(
      interrupt("Expression not found %s", span.text))

    val expCode = exp.translate(name, labels).instructions

    val s = target match {
      case v: VariableDeclaration =>
        if (v.global) {
          "aload_0\n%sputfield %s/%s %s\n".format(expCode, name, ref.identifier, referenceType.descriptor)
        } else {
          "%s%cstore %d\n".format(expCode, referenceType.prefix, v.ordinal + 1)
        }
      case p: Parameter =>
        "%s%cstore %d\n".format(expCode, referenceType.prefix, p.ordinal + 1)
      case _ =>
        interrupt("Non-value target for reference %s: %s", ref.identifier, target.span.text)
    }

    Code(withLine(s))
  }
}