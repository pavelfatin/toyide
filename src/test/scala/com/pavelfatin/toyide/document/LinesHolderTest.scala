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

import org.junit.Test
import org.junit.Assert._

class LinesHolderTest {
  @Test
  def linesCount() {
    assertEquals(1, Lines(0).linesCount)
    assertEquals(1, Lines(5).linesCount)
    assertEquals(2, Lines(5, 1).linesCount)
    assertEquals(3, Lines(5, 1, 3).linesCount)
    assertEquals(3, Lines(5, 1, 2).linesCount)
  }

  @Test
  def lineIndex() {
    assertEquals(0, Lines(0).lineNumberOf(0))
    assertEquals(0, Lines(1).lineNumberOf(0))
    assertEquals(0, Lines(2).lineNumberOf(1))
    assertEquals(0, Lines(5, 1).lineNumberOf(0))
    assertEquals(0, Lines(5, 1).lineNumberOf(1))
    assertEquals(1, Lines(5, 1).lineNumberOf(2))
    assertEquals(2, Lines(7, 1, 5).lineNumberOf(6))
    assertEquals(2, Lines(7, 1, 5).lineNumberOf(7))
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def lineIndexGreater() {
    Lines(0).lineNumberOf(1)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def lineIndexLess() {
    Lines(0).lineNumberOf(-1)
  }

  @Test
  def startOffset() {
    assertEquals(0, Lines(0).startOffsetOf(0))
    assertEquals(0, Lines(1).startOffsetOf(0))
    assertEquals(0, Lines(1, 0).startOffsetOf(0))
    assertEquals(1, Lines(1, 0).startOffsetOf(1))
    assertEquals(0, Lines(5, 1).startOffsetOf(0))
    assertEquals(2, Lines(5, 1).startOffsetOf(1))
    assertEquals(6, Lines(7, 1, 5).startOffsetOf(2))
    assertEquals(7, Lines(7, 6).startOffsetOf(1))
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def startOffsetGreater() {
    Lines(5).startOffsetOf(1)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def startOffsetLess() {
    Lines(5).startOffsetOf(-1)
  }

  @Test
  def endOffset() {
    assertEquals(0, Lines(0).endOffsetOf(0))
    assertEquals(1, Lines(1).endOffsetOf(0))
    assertEquals(1, Lines(5, 1).endOffsetOf(0))
    assertEquals(5, Lines(5, 1).endOffsetOf(1))
    assertEquals(5, Lines(7, 1, 5).endOffsetOf(1))
    assertEquals(7, Lines(7, 1, 5).endOffsetOf(2))
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def endOffsetGreater() {
    Lines(5).endOffsetOf(1)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def endOffsetLess() {
    Lines(5).endOffsetOf(-1)
  }

  @Test
  def toLocation() {
    assertEquals(Location(0, 0), Lines(0).toLocation(0))
    assertEquals(Location(0, 3), Lines(5).toLocation(3))
    assertEquals(Location(1, 2), Lines(7, 3).toLocation(6))
    assertEquals(Location(2, 3), Lines(9, 3, 5).toLocation(9))
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def loLocationGreater() {
    Lines(0).toLocation(1)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def loLocationLess() {
    Lines(0).toLocation(-1)
  }

  @Test
  def toOffset() {
    assertEquals(Some(0), Lines(0).toOffset(Location(0, 0)))
    assertEquals(None, Lines(0).toOffset(Location(0, 1)))
    assertEquals(None, Lines(0).toOffset(Location(1, 0)))
    assertEquals(Some(3), Lines(5).toOffset(Location(0, 3)))
    assertEquals(Some(6), Lines(7, 3).toOffset(Location(1, 2)))
    assertEquals(None, Lines(7, 3).toOffset(Location(2, 0)))
    assertEquals(None, Lines(7, 3).toOffset(Location(0, 4)))
  }

  @Test
  def toNearestOffset() {
    assertEquals(0, Lines(0).toNearestOffset(Location(0, 0)))
    assertEquals(0, Lines(0).toNearestOffset(Location(0, 1)))
    assertEquals(0, Lines(0).toNearestOffset(Location(1, 0)))
    assertEquals(3, Lines(5).toNearestOffset(Location(0, 3)))
    assertEquals(6, Lines(7, 3).toNearestOffset(Location(1, 2)))
    assertEquals(4, Lines(7, 3).toNearestOffset(Location(2, 0)))
    assertEquals(3, Lines(7, 3).toNearestOffset(Location(0, 4)))

    assertEquals(3, Lines(9, 3).toNearestOffset(Location(0, 9)))
    assertEquals(9, Lines(9, 3).toNearestOffset(Location(1, 9)))

    assertEquals(5, Lines(9, 3).toNearestOffset(Location(9, 1)))
  }

  @Test
  def maximumIndent() {
    assertEquals(0, Lines(0).maximumIndent)
    assertEquals(3, Lines(3).maximumIndent)
    assertEquals(3, Lines(5, 3).maximumIndent)
    assertEquals(4, Lines(8, 3).maximumIndent)
  }
}

