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