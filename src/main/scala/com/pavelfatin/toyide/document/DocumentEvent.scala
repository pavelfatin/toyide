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

sealed trait DocumentEvent {
  def undo(document: Document)

  def redo(document: Document)

  def asReplacement: Replacement
}

case class Insertion(offset: Int, chars: CharSequence) extends DocumentEvent {
  def undo(document: Document) {
    document.remove(offset, offset + chars.length)
  }

  def redo(document: Document) {
    document.insert(offset, chars.toString)
  }

  def asReplacement = Replacement(offset, offset, "", chars)
}

case class Removal(begin: Int, end: Int, before: CharSequence) extends DocumentEvent {
  def undo(document: Document) {
    document.insert(begin, before.toString)
  }

  def redo(document: Document) {
    document.remove(begin, end)
  }

  def asReplacement = Replacement(begin, end, before, "")
}

case class Replacement(begin: Int, end: Int, before: CharSequence, after: CharSequence) extends DocumentEvent {
  def undo(document: Document) {
    document.replace(begin, begin + after.length, before.toString)
  }

  def redo(document: Document) {
    document.replace(begin, end, after.toString)
  }

  def asReplacement = this
}
