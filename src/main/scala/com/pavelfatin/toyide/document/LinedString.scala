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

package com.pavelfatin.toyide.document

import LinedString._

private class LinedString private (val lines: List[CharSequence]) extends CharSequence {
  def this(s: String) {
    this(parseLines(s))
  }

  lazy val length = lines.foldLeft(0)(_ + _.length)

  def charAt(index: Int) = charAt(index, lines)

  def subSequence(start: Int, end: Int) = new LinedString(subLines(start, end))

  def concat(other: LinedString): LinedString = new LinedString(join(lines, other.lines))

  def replace(start: Int, end: Int, s: String): LinedString =
    new LinedString(replace(start, end, LinedString.parseLines(s)))

  lazy val wraps: Seq[Int] = wrapsIn(lines, 0)

  override lazy val toString = lines.foldLeft(new StringBuilder())(_ append _).toString()

  private def charAt(index: Int, list: List[CharSequence]): Char = list match {
    case Nil => throw new IndexOutOfBoundsException()
    case head :: tail => if (index < head.length) head.charAt(index) else charAt(index - head.length, tail)
  }

  private def subLines(start: Int, end: Int): List[CharSequence] = {
    if (start < 0 || end < 0) throw new IndexOutOfBoundsException()
    if (start > end) throw new IllegalArgumentException()
    if (start == end) {
      if (start <= length) List("") else throw new IndexOutOfBoundsException()
    } else {
      subLinesIn(start, end, lines)
    }
  }

  private def replace(start: Int, end: Int, lines: List[CharSequence]): List[CharSequence] = {
    val left = subLines(0, start)
    val right = subLines(end, length)
    join(join(left, lines), right)
  }
}

private object LinedString {
  private def parseLines(s: String): List[CharSequence] = {
    val i = s.indexOf('\n')
    if (i == - 1) s :: Nil else s.substring(0, i + 1) :: parseLines(s.substring(i + 1))
  }

  private def subLinesIn(start: Int, end: Int, lines: List[CharSequence]): List[CharSequence] = lines match {
    case Nil => throw new IndexOutOfBoundsException()
    case head :: tail =>
      val l = head.length
      if (start < l) {
        if (end <= l) {
          val line = head.subSequence(start, end)
          if (endsWith(line, '\n')) line :: "" :: Nil else line :: Nil
        }
        else {
          head.subSequence(start, l) :: subLinesIn(0, end - l, tail)
        }
      } else {
        subLinesIn(start - l, end - l, tail)
      }
  }

  private def join(prefix: List[CharSequence], suffix: List[CharSequence]): List[CharSequence] = {
    (prefix.reverse, suffix) match {
      case (pLast :: pInitReversed, sHead :: sTail) =>
        pInitReversed.reverse ::: List(pLast.toString + sHead.toString) ::: sTail
      case _ => prefix ::: suffix
    }
  }

  private def wrapsIn(lines: List[CharSequence], offset: Int): List[Int] = lines match {
    case Nil => throw new IllegalArgumentException()
    case _ :: Nil => Nil
    case (h :: t) => (offset + h.length - 1) :: wrapsIn(t, offset + h.length )
  }

  private def endsWith(chars: CharSequence, c: Char) =
    chars.length > 0 && chars.charAt(chars.length - 1) == c
}