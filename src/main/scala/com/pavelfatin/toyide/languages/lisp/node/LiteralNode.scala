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

package com.pavelfatin.toyide.languages.lisp.node

import com.pavelfatin.toyide.languages.lisp.core.NewList
import com.pavelfatin.toyide.languages.lisp.value._
import com.pavelfatin.toyide.node.NodeImpl

trait LiteralNode extends ExpressionNode {
  override def toString = "%s(%s)".format(kind, span.text)
}

class IntegerLiteralNode extends NodeImpl("integer") with LiteralNode {
  def read0(source: String) = IntegerValue(text.toInt)
}

class BooleanLiteralNode extends NodeImpl("boolean") with LiteralNode {
  def read0(source: String) = BooleanValue(text.toBoolean)
}

class CharacterLiteralNode extends NodeImpl("character") with LiteralNode {
  def read0(source: String) = {
    val c = text.substring(1) match {
      case "return" => '\r'
      case "newline" => '\n'
      case "tab" => '\t'
      case "space" => ' '
      case s => s.charAt(0)
    }
    CharacterValue(c)
  }
}

class StringLiteralNode extends NodeImpl("string") with LiteralNode {
  def read0(source: String) = {
    val s = text
    val characters = s.substring(1, s.length - 1)
      .replace("\\r", "\r")
      .replace("\\n", "\n")
      .replace("\\t", "\t")
      .replace("\\\\", "\\")
      .toSeq
    NewList(characters.map(CharacterValue(_)))
  }
}
