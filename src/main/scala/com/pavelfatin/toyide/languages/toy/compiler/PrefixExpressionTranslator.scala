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

import com.pavelfatin.toyide.languages.toy.node.PrefixExpression
import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.compiler.{Code, Labels}

trait PrefixExpressionTranslator extends ToyTranslatable { self: PrefixExpression =>
  override def translate(name: String, labels: Labels) = {
    val t = prefix.getOrElse(
      interrupt("Prefix token not found: %s", span.text))

    val exp = expression.getOrElse(
      interrupt("Inner expression not found: %s", span.text))

    val expCode = exp.translate(name, labels).instructions

    val l1 = labels.next()
    val l2 = labels.next()

    val s = t.kind match {
      case PLUS => expCode
      case MINUS => "%sineg\n".format(expCode)
      case BANG => "%sifne %s\niconst_1\ngoto %s\n%s:\niconst_0\n%s:\n".format(expCode, l1, l2, l1, l2)
      case _ => interrupt("Incorrect prefix: %s", t.span.text)
    }

    Code(withLine(s))
  }
}