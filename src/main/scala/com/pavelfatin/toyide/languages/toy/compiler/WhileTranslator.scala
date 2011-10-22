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