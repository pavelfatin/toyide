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