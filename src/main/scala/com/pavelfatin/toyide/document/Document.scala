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

import com.pavelfatin.toyide.{ObservableEvents, Interval}

trait Document extends LinesHolder with ObservableEvents[DocumentEvent] {
  var text: String

  def text(begin: Int, end: Int): String = characters.subSequence(begin, end).toString

  def text(interval: Interval): String = text(interval.begin, interval.end)

  def characters: CharSequence

  def charAt(offset: Int): Char = characters.charAt(offset)

  def charOptionAt(offset: Int) =
    if(offset >= 0 && offset < length) Some(charAt(offset)) else None

  def insert(offset: Int, s: String)

  def remove(begin: Int, end: Int)

  def remove(interval: Interval) {
    remove(interval.begin, interval.end)
  }

  def replace(begin: Int, end: Int, s: String)

  def replace(interval: Interval, s: String) {
    replace(interval.begin, interval.end, s)
  }

  def createAnchorAt(offset: Int, bias: Bias): Anchor
}

sealed trait Bias

object Bias {
  case object Left extends Bias

  case object Right extends Bias
}
