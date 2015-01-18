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

import com.pavelfatin.toyide.languages.lisp.LispTokens._
import com.pavelfatin.toyide.languages.lisp.core.{Quasiquote, Quote, Unquote, UnquoteSplicing}
import com.pavelfatin.toyide.languages.lisp.value.Expression
import com.pavelfatin.toyide.lexer.TokenKind
import com.pavelfatin.toyide.node.Node

trait QuotableNode { self: Node with ReadableNode =>
  protected def prefixKind: Option[TokenKind] =
    children.headOption.flatMap(_.token).map(_.kind).filter(Prefixes.contains)

  def quoted: Boolean = prefixKind.isDefined

  final def read(source: String) = {
    val value = read0(source)

    prefixKind collect {
      case QUOTE => Quote(value)
      case TILDE => Unquote(value)
      case TILDE_AT => UnquoteSplicing(value)
      case BACKQUOTE => Quasiquote(value)
    } getOrElse {
      value
    }
  }

  protected def read0(source: String): Expression

  protected def text: String = if (quoted) span.text.substring(children.head.span.length) else span.text
}
