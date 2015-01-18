/*
 * Copyright (C) 2014 Pavel Fatin <http://pavelfatin.com>
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

package com.pavelfatin.toyide.languages.lisp.examples

import com.pavelfatin.toyide.languages.lisp.value.HandleValue

class MockHandle extends HandleValue {
  private var _input = Seq.empty[Char]

  private var _builder = new StringBuilder()

  private var _closed = false

  def read(terminator: Option[Char]) = {
    val count = terminator.map(_input.indexOf(_) + 1).filter(_ > 0).getOrElse(_input.length)
    val (prefix, suffix) = _input.splitAt(count)
    _input = suffix
    prefix
  }

  def write(chars: Seq[Char]) {
    _builder ++= chars
  }

  def flush() {}

  def close() {
    _closed = true
  }

  def presentation = "MockHandle"

  def input = _input

  def input_=(s: String) {
    _input = s.toSeq
  }

  def output: String = _builder.toString().replace("\r\n", "\n")

  def closed: Boolean = _closed

  def reset() {
    _input = Seq.empty
    _builder = new StringBuilder()
    _closed = false
  }
}
