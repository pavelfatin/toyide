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

import com.pavelfatin.toyide.Interval

trait LinesHolder {
  def length: Int

  protected def wraps: Seq[Int]

  def linesCount = wraps.size + 1

  def lineNumberOf(offset: Int) = {
    if(offset < 0 || offset > length) throw new IndexOutOfBoundsException()
    wraps.view.takeWhile(_ < offset).size
  }

  def startOffsetOf(line: Int) = {
    if(line < 0 || line >= linesCount) throw new IndexOutOfBoundsException()
    if(line == 0) 0 else wraps(line - 1) + 1
  }

  def endOffsetOf(line: Int) = {
    if(line < 0 || line >= linesCount) throw new IndexOutOfBoundsException()
    if(line == wraps.size) length else wraps(line)
  }

  def intervalOf(line: Int) = {
    if(line < 0 || line >= linesCount) throw new IndexOutOfBoundsException()
    Interval(startOffsetOf(line), endOffsetOf(line))
  }

  def toLocation(offset: Int): Location = {
    if(offset < 0 || offset > length) throw new IndexOutOfBoundsException()
    val line = lineNumberOf(offset)
    Location(line, offset - startOffsetOf(line))
  }

  def toOffset(location: Location): Option[Int] = {
    if(location.line >= linesCount) return None
    val offset = startOffsetOf(location.line) + location.indent
    if(offset <= endOffsetOf(location.line)) Some(offset) else None
  }

  def toNearestOffset(location: Location): Int = {
    val line = location.line.min(linesCount - 1)
    val offset = startOffsetOf(line) + location.indent
    offset.min(endOffsetOf(line))
  }

  def maximumIndent = Range(0, linesCount).view
          .map(line => toLocation(endOffsetOf(line)).indent).max
}

case class Location(line: Int, indent: Int)