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
