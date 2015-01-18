/*
 * Copyright (C) 2014 Pavel Fatin <http://pavelfatin.com>
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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.Extensions._

object StringValue {
  def apply(s: String): ListValue =
    ListValue(s.map(CharacterValue))

  def unapply(list: ListValue): Option[String] = {
    val elements = list.content

    if (elements.isEmpty) None else elements.collectAll {
      case CharacterValue(c) => c
    } map { chars =>
      chars.mkString
    }
  }
}
