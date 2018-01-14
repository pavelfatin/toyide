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

import com.pavelfatin.toyide.languages.toy.node.While
import com.pavelfatin.toyide.compiler.{Code, Labels}

trait WhileTranslator extends ToyTranslatable { self: While =>
  override def translate(name: String, labels: Labels) = {
    val exp = expression.getOrElse(
      interrupt("Expression not found %s", span.text))

    val body = block.getOrElse(
      interrupt("Block not found %s", span.text))

    val l1 = labels.next()
    val l2 = labels.next()

    val s = "%s:\n%sifeq %s\n%sgoto %s\n%s:\n"
      .format(l1, exp.translate(name, labels).instructions, l2, body.translate(name, labels).instructions, l1, l2)

    Code(withLine(s))
  }
}