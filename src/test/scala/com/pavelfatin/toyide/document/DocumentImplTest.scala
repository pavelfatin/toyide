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

class DocumentImplTest {
  @Test
  def length() {
    assertEquals(0, new DocumentImpl("").length)
    assertEquals(1, new DocumentImpl("a").length)
    assertEquals(2, new DocumentImpl("ab").length)
    assertEquals(3, new DocumentImpl("ab\n").length)
    assertEquals(4, new DocumentImpl("ab\nc").length)
  }
  
  @Test
  def linesCount() {
    assertEquals(1, new DocumentImpl("").linesCount)
    assertEquals(1, new DocumentImpl("ab").linesCount)
    assertEquals(2, new DocumentImpl("ab\n").linesCount)
    assertEquals(2, new DocumentImpl("ab\ncd").linesCount)
    assertEquals(3, new DocumentImpl("ab\ncd\n").linesCount)
    assertEquals(3, new DocumentImpl("ab\ncd\ne").linesCount)
  }
  
  @Test
  def lineNumber() {
    assertEquals(0, new DocumentImpl("").lineNumberOf(0))
    assertEquals(0, new DocumentImpl("a").lineNumberOf(1))
    
    assertEquals(0, new DocumentImpl("abc").lineNumberOf(0))
    assertEquals(0, new DocumentImpl("abc").lineNumberOf(1))
    assertEquals(0, new DocumentImpl("abc").lineNumberOf(2))
    assertEquals(0, new DocumentImpl("abc").lineNumberOf(3))
    
    assertEquals(0, new DocumentImpl("abc\n").lineNumberOf(3))
    assertEquals(1, new DocumentImpl("abc\n").lineNumberOf(4))
    
    assertEquals(0, new DocumentImpl("\n").lineNumberOf(0))
    assertEquals(1, new DocumentImpl("\n").lineNumberOf(1))
  }
  
  @Test
  def startOffset() {
    assertEquals(0, new DocumentImpl("").startOffsetOf(0))
    assertEquals(0, new DocumentImpl("a").startOffsetOf(0))
    assertEquals(0, new DocumentImpl("ab").startOffsetOf(0))
    assertEquals(0, new DocumentImpl("ab\n").startOffsetOf(0))
    assertEquals(0, new DocumentImpl("ab\nc").startOffsetOf(0))
    
    assertEquals(3, new DocumentImpl("ab\n").startOffsetOf(1))
    assertEquals(3, new DocumentImpl("ab\nc").startOffsetOf(1))
    assertEquals(3, new DocumentImpl("ab\ncd").startOffsetOf(1))
    assertEquals(3, new DocumentImpl("ab\ncd\n").startOffsetOf(1))
    assertEquals(3, new DocumentImpl("ab\ncd\ne").startOffsetOf(1))
    
    assertEquals(6, new DocumentImpl("ab\ncd\n").startOffsetOf(2))
  }  
  
  @Test
  def endOffset() {
    assertEquals(0, new DocumentImpl("").endOffsetOf(0))
    assertEquals(1, new DocumentImpl("a").endOffsetOf(0))
    assertEquals(2, new DocumentImpl("ab").endOffsetOf(0))
    assertEquals(2, new DocumentImpl("ab\n").endOffsetOf(0))
    assertEquals(2, new DocumentImpl("ab\nc").endOffsetOf(0))
    
    assertEquals(3, new DocumentImpl("ab\n").endOffsetOf(1))
    assertEquals(4, new DocumentImpl("ab\nc").endOffsetOf(1))
    assertEquals(5, new DocumentImpl("ab\ncd").endOffsetOf(1))
    assertEquals(5, new DocumentImpl("ab\ncd\n").endOffsetOf(1))
    
    assertEquals(6, new DocumentImpl("ab\ncd\n").endOffsetOf(2))
  }

  @Test
  def text() {
    val document = new DocumentImpl()
    document.text = "foo"
    assertEquals("foo", document.text)
    document.text = "bar"
    assertEquals("bar", document.text)
  }

  @Test
  def insert() {
    val document = new DocumentImpl()
    document.insert(0, "foo")
    assertEquals("foo", document.text)
  }

  @Test
  def insertBefore() {
    val document = new DocumentImpl("bar")
    document.insert(0, "foo")
    assertEquals("foobar", document.text)
  }

  @Test
  def insertAfter() {
    val document = new DocumentImpl("foo")
    document.insert(3, "bar")
    assertEquals("foobar", document.text)
  }

  @Test
  def insertBetween() {
    val document = new DocumentImpl("foomoo")
    document.insert(3, "bar")
    assertEquals("foobarmoo", document.text)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def insertOffsetNegative() {
    val document = new DocumentImpl("foo")
    document.insert(-1, "foo")
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def insertOffsetOut() {
    val document = new DocumentImpl("foo")
    document.insert(4, "foo")
  }

  @Test
  def remove() {
    val document = new DocumentImpl("foobarmoo")
    document.remove(3, 6)
    assertEquals("foomoo", document.text)
  }

  @Test
  def removeEmpty() {
    val document = new DocumentImpl("foobar")
    document.remove(3, 3)
    assertEquals("foobar", document.text)
  }

  @Test
  def removeAll() {
    val document = new DocumentImpl("foo")
    document.remove(0, 3)
    assertEquals("", document.text)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def removeBeginNegative() {
    val document = new DocumentImpl("foo")
    document.remove(-1, 0)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def removeBeginOut() {
    val document = new DocumentImpl("foo")
    document.remove(4, 3)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def removeEndNegative() {
    val document = new DocumentImpl("foo")
    document.remove(0, -1)
  }

  @Test(expected = classOf[IllegalArgumentException])
  def removeNegativeInterval() {
    val document = new DocumentImpl("foo")
    document.remove(2, 1)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def removeEndOut() {
    val document = new DocumentImpl("foo")
    document.remove(0, 4)
  }

  @Test
  def replace() {
    val document = new DocumentImpl("foobarmoo")
    document.replace(3, 6, "goo")
    assertEquals("foogoomoo", document.text)
  }

  @Test
  def replaceWithLonger() {
    val document = new DocumentImpl("foobarmoo")
    document.replace(3, 6, "12345")
    assertEquals("foo12345moo", document.text)
  }

  @Test
  def replaceWithShorter() {
    val document = new DocumentImpl("foobarmoo")
    document.replace(3, 6, "_")
    assertEquals("foo_moo", document.text)
  }

  @Test
  def replaceWithEmpty() {
    val document = new DocumentImpl("foobarmoo")
    document.replace(3, 6, "")
    assertEquals("foomoo", document.text)
  }

  @Test
  def replaceEmpty() {
    val document = new DocumentImpl("foomoo")
    document.replace(3, 3, "bar")
    assertEquals("foobarmoo", document.text)
  }

  @Test
  def replaceAll() {
    val document = new DocumentImpl("foo")
    document.replace(0, 3, "bar")
    assertEquals("bar", document.text)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def replaceBeginNegative() {
    val document = new DocumentImpl("foo")
    document.replace(-1, 0, "")
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def replaceBeginOut() {
    val document = new DocumentImpl("foo")
    document.replace(4, 3, "")
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def replaceEndNegative() {
    val document = new DocumentImpl("foo")
    document.replace(0, -1, "")
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def replaceEndOut() {
    val document = new DocumentImpl("foo")
    document.replace(0, 4, "")
  }

  @Test(expected = classOf[IllegalArgumentException])
  def replaceNegativeInterval() {
    val document = new DocumentImpl("foo")
    document.replace(2, 1, "")
  }
}