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

package com.pavelfatin.toyide.lexer

import com.pavelfatin.toyide.Span

abstract class AbstractTokenIterator(input: CharSequence) extends Iterator[Token] {
  private var index = 0
  private var marker = -1

  def advance() {
    advance(1)
  }

  def advance(count: Int) {
    index += count
  }

  def mark() {
    marker = index
  }

  def marked: Span = Span(input, marker, index)

  def captureChar: Span = captureChars(1)

  def captureChars(count: Int) = {
    mark()
    Range(0, count).foreach(n => advance())
    marked
  }

  def skip(predicate: Char => Boolean) {
    while (hasNext && predicate(char)) advance()
  }

  def capture(predicate: Char => Boolean): Span = {
    mark()
    skip(predicate)
    marked
  }

  def char: Char = input.charAt(index)

  def ahead(offset: Int): Option[Char] = {
    val i = index + offset
    if(i < input.length) Some(input.charAt(i)) else None
  }

  def isAhead(char: Char): Boolean = isAhead(_ == char)

  def isAhead(predicate: Char => Boolean): Boolean = ahead(1).exists(predicate)

  def isAhead(string: String): Boolean = {
    val end = index + string.length
    end <= input.length && input.subSequence(index, end).toString == string
  }

  def hasNext: Boolean = index < input.length
}
