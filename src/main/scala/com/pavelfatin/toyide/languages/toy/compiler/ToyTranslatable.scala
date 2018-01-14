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

import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.compiler._
import com.pavelfatin.toyide.node._

trait ToyTranslatable extends Translatable { self: Node =>
  protected def interrupt(message: String, values: Any*): Nothing =
    throw new TranslationException(message.format(values: _*))

  protected def withLine(s: String): String = {
    val line = self.span.source.take(self.span.begin).count(_ == '\n')
    ".line %d\n%s".format(line, s)
  }
}
