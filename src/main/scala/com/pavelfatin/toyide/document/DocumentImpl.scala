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

class DocumentImpl(s: String = "") extends Document {
  private var ls = new LinedString(s)

  private var anchors = List[AnchorImpl]()

  def length = ls.length

  def text = ls.toString

  def text_=(s: String) {
    replace(0, length, s)
  }

  def characters: CharSequence = ls

  def insert(offset: Int, s: String) {
    check(offset)
    ls = ls.replace(offset, offset, s)
    updateAnchors(offset, offset, offset + s.length)
    notifyObservers(Insertion(offset, s))
  }

  def remove(begin: Int, end: Int) {
    check(begin, end)
    val previous = ls.subSequence(begin, end)
    ls = ls.replace(begin, end, "")
    updateAnchors(begin, end, begin)
    notifyObservers(Removal(begin, end, previous))
  }

  def replace(begin: Int, end: Int, s: String) {
    check(begin, end)
    val previous = ls.subSequence(begin, end)
    ls = ls.replace(begin, end, s)
    updateAnchors(begin, end, begin + s.length)
    notifyObservers(Replacement(begin, end, previous, s))
  }

  private def updateAnchors(begin: Int, end: Int, end2: Int) {
    anchors.foreach(_.update(begin, end, end2))
  }

  private def check(offset: Int, parameter: String = "Offset") {
    if(offset < 0 || offset > length)
      throw new IndexOutOfBoundsException("%s (%d) must be withing [%d; %d]".format(parameter, offset, 0, length))
  }

  private def check(begin: Int, end: Int) {
    check(begin, "Begin")
    check(end, "End")
    if(begin > end)
      throw new IllegalArgumentException("Begin (%d) must be not greater than end (%d)".format(begin, end))
  }

  def createAnchorAt(offset: Int, bias: Bias): Anchor = {
    val anchor = new AnchorImpl(offset, bias)
    anchors ::= anchor
    anchor
  }

  protected def wraps = ls.wraps

  private class AnchorImpl(var offset: Int, bias: Bias) extends Anchor {
    def dispose() {
      anchors = anchors.diff(Seq(this))
    }

    def update(begin: Int, end: Int, end2: Int) {
      if (begin < offset && end <= offset) {
        offset += end2 - end
      } else if ((begin < offset && offset < end && end2 < offset) ||
        (begin == end && begin == offset && bias == Bias.Right)) {
        offset = end2
      }
    }
  }
}
