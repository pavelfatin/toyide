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
