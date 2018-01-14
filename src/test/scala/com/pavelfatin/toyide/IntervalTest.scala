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

package com.pavelfatin.toyide

import org.junit.Test
import org.junit.Assert._

class IntervalTest {
  @Test(expected = classOf[IllegalArgumentException])
  def negativeBegin() {
    Interval(-1, 0)
  }

  @Test(expected = classOf[IllegalArgumentException])
  def negativeEnd() {
    Interval(0, -1)
  }

  @Test(expected = classOf[IllegalArgumentException])
  def negativeLength() {
    Interval(1, 0)
  }

  @Test
  def length() {
    assertEquals(0, Interval(3, 3).length)
    assertEquals(1, Interval(3, 4).length)
    assertEquals(2, Interval(3, 5).length)
    assertEquals(3, Interval(3, 6).length)
  }

  @Test
  def empty() {
    assertTrue(Interval(3, 3).empty)
    assertFalse(Interval(3, 4).empty)
    assertFalse(Interval(3, 5).empty)
    assertFalse( Interval(3, 6).empty)
  }

  @Test
  def includes() {
    assertFalse(Interval(5, 5).includes(5))

    assertFalse(Interval(5, 6).includes(4))
    assertTrue(Interval(5, 6).includes(5))
    assertFalse(Interval(5, 6).includes(6))

    assertFalse(Interval(5, 7).includes(4))
    assertTrue(Interval(5, 7).includes(5))
    assertTrue(Interval(5, 7).includes(6))
    assertFalse(Interval(5, 7).includes(7))

    assertFalse(Interval(5, 8).includes(4))
    assertTrue(Interval(5, 8).includes(5))
    assertTrue(Interval(5, 8).includes(6))
    assertTrue(Interval(5, 8).includes(7))
    assertFalse(Interval(5, 8).includes(8))
  }

  @Test
  def includesInterval() {
    assertFalse(Interval(5, 5).includes(Interval(5, 5)))
    assertFalse(Interval(5, 7).includes(Interval(6, 6)))
    assertFalse(Interval(5, 5).includes(Interval(5, 7)))

    assertFalse(Interval(5, 7).includes(Interval(4, 5)))
    assertTrue(Interval(5, 7).includes(Interval(5, 6)))

    assertFalse(Interval(5, 7).includes(Interval(7, 8)))
    assertTrue(Interval(5, 7).includes(Interval(6, 7)))

    assertTrue(Interval(5, 7).includes(Interval(5, 7)))
    assertTrue(Interval(5, 7).includes(Interval(5, 6)))
    assertTrue(Interval(5, 7).includes(Interval(6, 7)))

    assertFalse(Interval(5, 7).includes(Interval(4, 7)))
    assertFalse(Interval(5, 7).includes(Interval(5, 8)))
    assertFalse(Interval(5, 7).includes(Interval(4, 8)))
  }

  @Test
  def intersectsWith() {
    assertNotIntersect(Interval(5, 5), Interval(5, 5))
    assertNotIntersect(Interval(4, 4), Interval(5, 5))
    assertNotIntersect(Interval(6, 6), Interval(5, 5))

    assertNotIntersect(Interval(5, 5), Interval(3, 7))

    assertNotIntersect(Interval(3, 3), Interval(3, 7))
    assertNotIntersect(Interval(6, 6), Interval(3, 7))

    assertNotIntersect(Interval(0, 3), Interval(3, 7))
    assertNotIntersect(Interval(7, 10), Interval(3, 7))

    assertIntersect(Interval(3, 7), Interval(3, 7))

    assertIntersect(Interval(0, 4), Interval(3, 7))
    assertIntersect(Interval(6, 9), Interval(3, 7))

    assertIntersect(Interval(0, 5), Interval(3, 7))
    assertIntersect(Interval(5, 9), Interval(3, 7))

    assertIntersect(Interval(0, 9), Interval(3, 7))
    assertIntersect(Interval(4, 6), Interval(3, 7))
    assertIntersect(Interval(3, 4), Interval(3, 7))
    assertIntersect(Interval(6, 7), Interval(3, 7))

    assertIntersect(Interval(4, 5), Interval(3, 7))
  }

  protected def assertIntersect(a: Interval, b: Interval) {
    assertTrue(a.intersectsWith(b))
    assertTrue(b.intersectsWith(a))
  }

  protected def assertNotIntersect(a: Interval, b: Interval) {
    assertFalse(a.intersectsWith(b))
    assertFalse(b.intersectsWith(a))
  }
}