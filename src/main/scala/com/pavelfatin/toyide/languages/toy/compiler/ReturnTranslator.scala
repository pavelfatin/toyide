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

import com.pavelfatin.toyide.languages.toy.node.Return
import com.pavelfatin.toyide.compiler.{Code, Labels}

trait ReturnTranslator extends ToyTranslatable { self: Return =>
  override def translate(name: String, labels: Labels) = {
    val returnCode = expression.map { exp =>
      val t = exp.nodeType.getOrElse(
        interrupt("Unknown return expression type: %s", span.text))

      "%s\n%creturn\n".format(exp.translate(name, labels).instructions, t.prefix)
    }
    Code(withLine(returnCode.getOrElse("return\n")))
  }
}