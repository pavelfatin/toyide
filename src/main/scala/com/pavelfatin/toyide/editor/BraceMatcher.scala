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

private trait BraceMatcher {
  def braceTypeOf(token: Token, tokens: Seq[Token], offset: Int): BraceType

  def complementIn(tail: Seq[Token], opening: TokenKind, closing: TokenKind): Option[Token]
}

private abstract sealed class BraceType

private case object Inapplicable extends BraceType

private case object Paired extends BraceType

private case object Unbalanced extends BraceType
