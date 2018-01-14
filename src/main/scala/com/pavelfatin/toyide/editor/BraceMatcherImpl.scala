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