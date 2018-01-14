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
