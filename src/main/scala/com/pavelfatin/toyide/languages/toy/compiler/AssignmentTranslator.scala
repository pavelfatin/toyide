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