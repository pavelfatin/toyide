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

package com.pavelfatin.toyide.editor

import com.pavelfatin.toyide.lexer.{TokenKind, Token}

private class BraceMatcherImpl(complements: Seq[(TokenKind, TokenKind)]) extends BraceMatcher {
  def braceTypeOf(token: Token, tokens: Seq[Token], offset: Int): BraceType = {
    def right(complement: (TokenKind, TokenKind)): Option[BraceType] = {
      if(token.kind != complement._1) return None

      val tail = tokens.dropWhile(!_.eq(token)).tail

      complementIn(tail, complement._1, complement._2).map { it =>
        if (token.span.begin == offset || it.span.end == offset) Paired else Inapplicable
      } orElse Some {
        if(token.span.begin == offset) Unbalanced else Inapplicable
      }
    }

    def left(complement: (TokenKind, TokenKind)): Option[BraceType] = {
      if(token.kind != complement._2) return None

      val tail = tokens.takeWhile(!_.eq(token)).reverse

      complementIn(tail, complement._2, complement._1).map { it =>
        if (token.span.end == offset || it.span.begin == offset) Paired else Inapplicable
      } orElse Some {
        if(token.span.end == offset) Unbalanced else Inapplicable
      }
    }

    val variants = complements.view.flatMap(right) ++ complements.view.flatMap(left)

    variants.headOption.getOrElse(Inapplicable)
  }

  def complementIn(tail: Seq[Token], opening: TokenKind, closing: TokenKind): Option[Token] = {
    var level = 0
    tail.foreach { it =>
      if(it.kind == opening) level += 1
      if(it.kind == closing) {
        if(level == 0) return Some(it)
        level -= 1
      }
    }
    None
  }
}