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
