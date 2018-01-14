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
