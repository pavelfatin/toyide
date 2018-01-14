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

class LinedStringTest {
  @Test
  def construction() {
    assertEquals(List(""), ls("").lines)
    assertEquals(List("foo"), ls("foo").lines)
    assertEquals(List("foo\n", ""), ls("foo\n").lines)
    assertEquals(List("foo\n", "bar"), ls("foo\nbar").lines)
    assertEquals(List("foo\n", "bar\n", ""), ls("foo\nbar\n").lines)
    assertEquals(List("foo\n", "bar\n", "moo"), ls("foo\nbar\nmoo").lines)
    assertEquals(List("foo\n","bar\n", "moo\n", ""), ls("foo\nbar\nmoo\n").lines)
    assertEquals(List("\n", ""), ls("\n").lines)
    assertEquals(List("\n", "\n", ""), ls("\n\n").lines)
    assertEquals(List("\n", "\n", "\n", ""), ls("\n\n\n").lines)
  }
  
  @Test
  def length() {
    assertEquals(0, ls("").length)
    assertEquals(3, ls("foo").length)
    assertEquals(6, ls("foo\nam").length)
    assertEquals(10, ls("foo\nbar\nam").length)
  }

  @Test
  def charAt() {
    assertEquals('a', ls("a").charAt(0))
    assertEquals('a', ls("ab").charAt(0))
    assertEquals('b', ls("ab").charAt(1))
    assertEquals('a', ls("abc").charAt(0))
    assertEquals('b', ls("abc").charAt(1))
    assertEquals('c', ls("abc").charAt(2))
    assertEquals('a', ls("a\nb\nc").charAt(0))
    assertEquals('\n', ls("a\nb\nc").charAt(1))
    assertEquals('b', ls("a\nb\nc").charAt(2))
    assertEquals('\n', ls("a\nb\nc").charAt(3))
    assertEquals('c', ls("a\nb\nc").charAt(4))
    assertEquals('b', ls("foo\nbar\ngoo").charAt(4))
    assertEquals('r', ls("foo\nbar\ngoo").charAt(6))
    assertEquals('g', ls("foo\nbar\ngoo").charAt(8))
  }

  @Test
  def asString() {
    assertEquals("", ls("").toString)
    assertEquals("\n", ls("\n").toString)
    assertEquals("\n\n", ls("\n\n").toString)
    assertEquals("\n\n\n", ls("\n\n\n").toString)
    assertEquals("foo", ls("foo").toString)
    assertEquals("foo\n", ls("foo\n").toString)
    assertEquals("foo\nbar", ls("foo\nbar").toString)
    assertEquals("foo\nbar\n", ls("foo\nbar\n").toString)
    assertEquals("foo\nbar\nmoo", ls("foo\nbar\nmoo").toString)
    assertEquals("foo\nbar\nmoo\n", ls("foo\nbar\nmoo\n").toString)
    assertEquals("foo\nbar\nmoo\n\n", ls("foo\nbar\nmoo\n\n").toString)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def charAtNegativeIndex() {
    ls("").charAt(-1)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def charAtGreaterIndex() {
    ls("foo").charAt(3)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def charAtGreaterIndexChained() {
    ls("foo\nbar").charAt(7)
  }

  @Test
  def subSequence() {
    assertEquals(List(""), ls("").subSequence(0, 0).lines)
    assertEquals(List("a"), ls("a").subSequence(0, 1).lines)
    assertEquals(List("foo"), ls("foo").subSequence(0, 3).lines)

    assertEquals(List(""), ls("foo").subSequence(0, 0).lines)
    assertEquals(List("f"), ls("foo").subSequence(0, 1).lines)
    assertEquals(List("fo"), ls("foo").subSequence(0, 2).lines)
    assertEquals(List("oo"), ls("foo").subSequence(1, 3).lines)

    assertEquals(List(""), ls("\n").subSequence(0, 0).lines)
    assertEquals(List("\n", ""), ls("\n").subSequence(0, 1).lines)
    assertEquals(List("\n", ""), ls("\n\n").subSequence(0, 1).lines)
    assertEquals(List("\n", "\n", ""), ls("\n\n").subSequence(0, 2).lines)

    assertEquals(List(""), ls("foo\nbar").subSequence(4, 4).lines)
    assertEquals(List("b"), ls("foo\nbar").subSequence(4, 5).lines)
    assertEquals(List("ba"), ls("foo\nbar").subSequence(4, 6).lines)
    assertEquals(List("ar"), ls("foo\nbar").subSequence(5, 7).lines)

    assertEquals(List("foo\n", "bar"), ls("foo\nbar").subSequence(0, 7).lines)
    assertEquals(List("foo\n", ""), ls("foo\nbar").subSequence(0, 4).lines)
    assertEquals(List("\n", "bar"), ls("foo\nbar").subSequence(3, 7).lines)

    assertEquals(List("foo\n", "bar\n", "goo"), ls("foo\nbar\ngoo").subSequence(0, 11).lines)
    assertEquals(List("oo\n", "bar\n", "goo"), ls("foo\nbar\ngoo").subSequence(1, 11).lines)
    assertEquals(List("foo\n", "bar\n", "go"), ls("foo\nbar\ngoo").subSequence(0, 10).lines)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def subSequenceNegativeStart() {
    ls("").subSequence(-1, 0)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def subSequenceNegativeEnd() {
    ls("").subSequence(0, -1)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def subSequenceGreaterStart() {
    ls("foo").subSequence(4, 4)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def subSequenceGreaterEnd() {
    ls("foo").subSequence(3, 4)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def subSequenceGreaterOnEmpty() {
    ls("").subSequence(0, 1)
  }

  @Test(expected = classOf[IllegalArgumentException])
  def subSequenceNegativeInterval() {
    ls("foo").subSequence(3, 2)
  }

   @Test
  def concat() {
    assertEquals(List(""), ls("").concat(ls("")).lines)

    assertEquals(List("foo"), ls("foo").concat(ls("")).lines)
    assertEquals(List("foo\n", ""), ls("foo\n").concat(ls("")).lines)

    assertEquals(List("foo\n", "bar"), ls("foo\nbar").concat(ls("")).lines)
    assertEquals(List("foo\n", "bar\n", ""), ls("foo\nbar\n").concat(ls("")).lines)

    assertEquals(List("foo"), ls("").concat(ls("foo")).lines)
    assertEquals(List("foo\n", ""), ls("").concat(ls("foo\n")).lines)

    assertEquals(List("foo\n", "bar"), ls("").concat(ls("foo\nbar")).lines)
    assertEquals(List("foo\n", "bar\n", ""), ls("").concat(ls("foo\nbar\n")).lines)

    assertEquals(List("foobar"), ls("foo").concat(ls("bar")).lines)
    assertEquals(List("foobar\n", ""), ls("foo").concat(ls("bar\n")).lines)
    assertEquals(List("foo\n", "bar"), ls("foo\n").concat(ls("bar")).lines)
    assertEquals(List("foo\n", "bar\n", ""), ls("foo\n").concat(ls("bar\n")).lines)

    assertEquals(List("foo\n", ""), ls("foo").concat(ls("\n")).lines)
    assertEquals(List("\n", "foo"), ls("\n").concat(ls("foo")).lines)

    assertEquals(List("foo\n", "barmoo\n", "goo"), ls("foo\nbar").concat(ls("moo\ngoo")).lines)
    assertEquals(List("foo\n", "bar\n", "moo\n", "goo"), ls("foo\nbar\n").concat(ls("moo\ngoo")).lines)
  }

  @Test
  def replace() {
    assertEquals(List("foo"), ls("").replace(0, 0, "foo").lines)
    assertEquals(List("bar"), ls("foo").replace(0, 3, "bar").lines)
    assertEquals(List(""), ls("foo").replace(0, 3, "").lines)
    assertEquals(List("moo"), ls("foo").replace(0, 1, "m").lines)

    assertEquals(List("foo\n", "bmoo\n", "goor"), ls("foo\nbar").replace(5, 6, "moo\ngoo").lines)

    assertEquals(List("foo\n", ""), ls("\n").replace(0, 0, "foo").lines)
    assertEquals(List("\n", "foo"), ls("\n").replace(1, 1, "foo").lines)
  }

  @Test
  def wraps() {
    assertEquals(List(), ls("").wraps)
    assertEquals(List(0), ls("\n").wraps)
    assertEquals(List(0, 1), ls("\n\n").wraps)
    assertEquals(List(), ls("foo").wraps)
    assertEquals(List(3), ls("foo\n").wraps)
    assertEquals(List(3), ls("foo\nbar").wraps)
    assertEquals(List(3, 7), ls("foo\nbar\nmoo").wraps)
    assertEquals(List(3, 7, 12), ls("foo\nbar\nmooo\ngoo").wraps)
  }

  @Test
  def immutability() {
    val original = ls("foo")
    original.replace(0, 3, "bar")
    assertEquals(List("foo"), original.lines)
  }

  @Test
  def linesIdentity() {
    val original = ls("foo\nbar\nmoo")
    val replaced = original.replace(4, 7, "goo")
    assertTrue(original.lines(0).eq(replaced.lines(0)))
    assertTrue(original.lines(2).eq(replaced.lines(2)))
  }

  private def ls(s: String): LinedString = new LinedString(s)
}