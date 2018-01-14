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

package com.pavelfatin.toyide.editor

import org.junit.Test
import org.junit.Assert._
import com.pavelfatin.toyide.Helpers._
import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.Interval

class HistoryImplTest {
  @Test
  def initialState() {
    val history = new HistoryImpl()
    assertFalse(history.canUndo)
    assertFalse(history.canRedo)
  }

  @Test(expected = classOf[IllegalStateException])
  def illegalUndo() {
    new HistoryImpl().undo()
  }

  @Test(expected = classOf[IllegalStateException])
  def illegalRedo() {
    new HistoryImpl().undo()
  }

  @Test(expected = classOf[IllegalStateException])
  def nestedRecording() {
    val history = new HistoryImpl()
    val (document, terminal) = parseDocument("|")
    history.recording(document, terminal) {
      history.recording(document, terminal) {}
    }
  }

  def caretMovementsAreNotRecorded() {
    val history = new HistoryImpl()
    val (document, terminal) = parseDocument("|foo")
    history.recording(document, terminal) {
      terminal.offset = 1
    }
    assertFalse(history.canUndo)
  }

  @Test
  def insert() {
    assertEffectsAre("|", "|foo") { (document, terminal) =>
      document.insert(0, "foo")
    }
    assertEffectsAre("|fooMoo", "|fooBarMoo") { (document, terminal) =>
      document.insert(3, "Bar")
    }
  }

  @Test
  def remove() {
    assertEffectsAre("|foo", "|") { (document, terminal) =>
      document.remove(0, 3)
    }
    assertEffectsAre("|fooBarMoo", "|fooMoo") { (document, terminal) =>
      document.remove(3, 6)
    }
  }

  @Test
  def replace() {
    assertEffectsAre("|foo", "|bar") { (document, terminal) =>
      document.replace(0, 3, "bar")
    }
    assertEffectsAre("|fooBarMoo", "|fooGooMoo") { (document, terminal) =>
      document.replace(3, 6, "Goo")
    }
  }

  @Test
  def caret() {
    assertEffectsAre("|", "foo|") { (document, terminal) =>
      document.insert(0, "foo")
      terminal.offset += 3
    }
    assertEffectsAre("foo|", "|") { (document, terminal) =>
      terminal.offset -= 3
      document.remove(0, 3)
    }
    assertEffectsAre("|foo", "bar|") { (document, terminal) =>
      document.replace(0, 3, "bar")
      terminal.offset += 3
    }
  }

  @Test
  def selection() {
    assertEffectsAre("|", "[|foo]") { (document, terminal) =>
      document.insert(0, "foo")
      terminal.selection = Some(Interval(0, 3))
    }
    assertEffectsAre("[|foo]", "|") { (document, terminal) =>
      terminal.selection = None
      document.remove(0, 3)
    }
    assertEffectsAre("[|foo]", "|bar") { (document, terminal) =>
      document.replace(0, 3, "bar")
      terminal.selection = None
    }
    assertEffectsAre("|foo", "[|bar]") { (document, terminal) =>
      document.replace(0, 3, "bar")
      terminal.selection = Some(Interval(0, 3))
    }
    assertEffectsAre("[|foo]", "|b[ar]") { (document, terminal) =>
      document.replace(0, 3, "bar")
      terminal.selection = Some(Interval(1, 3))
    }
  }

  @Test
  def sequence() {
    assertEffectsAre("|", "Q|ED[ob]r") { (document, terminal) =>
      document.insert(0, "bar")
      document.insert(0, "foo")
      terminal.offset = 3
      terminal.selection = Some(Interval(1, 3))
      document.remove(1, 2)
      document.remove(3, 4)
      document.replace(0, 1, "QED")
      terminal.offset = 1
      terminal.selection = Some(Interval(3, 5))
    }
  }

  @Test
  def sequenceOrder() {
    assertEffectsAre("|", "|") { (document, terminal) =>
      document.insert(0, "foo")
      document.remove(0, 3)
    }
    assertEffectsAre("|foo", "|foo") { (document, terminal) =>
      document.remove(0, 3)
      document.insert(0, "foo")
    }
  }

  @Test
  def undoRedo() {
    val history = new HistoryImpl()

    val (document, terminal) = parseDocument("|")

    history.recording(document, terminal)(document.insert(0, "a"))
    assertTrue(history.canUndo)
    assertFalse(history.canRedo)
    assertEquals("|a", formatDocument(document, terminal))

    history.recording(document, terminal)(document.insert(1, "b"))
    assertTrue(history.canUndo)
    assertFalse(history.canRedo)
    assertEquals("|ab", formatDocument(document, terminal))

    history.undo()
    assertTrue(history.canUndo)
    assertTrue(history.canRedo)
    assertEquals("|a", formatDocument(document, terminal))

    history.redo()
    assertTrue(history.canUndo)
    assertFalse(history.canRedo)
    assertEquals("|ab", formatDocument(document, terminal))

    history.undo()
    assertTrue(history.canUndo)
    assertTrue(history.canRedo)
    assertEquals("|a", formatDocument(document, terminal))

    history.undo()
    assertFalse(history.canUndo)
    assertTrue(history.canRedo)
    assertEquals("|", formatDocument(document, terminal))
  }

  @Test
  def recordingClearsRedo() {
    val history = new HistoryImpl()

    val (document, terminal) = parseDocument("|")

    history.recording(document, terminal)(document.insert(0, "a"))
    history.recording(document, terminal)(document.insert(1, "b"))
    history.undo()

    history.recording(document, terminal)(document.insert(1, "c"))
    assertTrue(history.canUndo)
    assertFalse(history.canRedo)
    assertEquals("|ac", formatDocument(document, terminal))

    history.undo()
    assertTrue(history.canUndo)
    assertTrue(history.canRedo)
    assertEquals("|a", formatDocument(document, terminal))

    history.redo()
    assertTrue(history.canUndo)
    assertFalse(history.canRedo)
    assertEquals("|ac", formatDocument(document, terminal))
  }

  protected def assertEffectsAre(before: String, after: String)(action: (Document, Terminal) => Unit) {
    val (document, terminal) = parseDocument(before)

    val history = new HistoryImpl()

    history.recording(document, terminal) {
      action(document, terminal)
    }

    assertEquals(after, formatDocument(document, terminal)) // assert that action produces expected changes

    history.undo()
    assertEquals(before, formatDocument(document, terminal))

    history.redo()
    assertEquals(after, formatDocument(document, terminal))
  }
}