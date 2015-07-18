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

package com.pavelfatin.toyide

import org.junit.Test
import org.junit.Assert._

class MockLexerTest {
  @Test
  def empty() {
    assertTokens("", "")
  }

  @Test
  def letter() {
    assertTokens("A", "token(A)")
  }

  @Test
  def letters() {
    assertTokens("AB", "token(A), token(B)")
  }

  @Test
  def braces() {
    assertTokens("()", "token((), token())")
    assertTokens("(Foo)", "token((), token(Foo), token())")
    assertTokens("{}", "token({), token(})")
  }

  @Test
  def single() {
    assertTokens("Foo", "token(Foo)")
  }

  @Test
  def several() {
    assertTokens("FooBar", "token(Foo), token(Bar)")
  }

  @Test
  def gap() {
    assertTokens("Foo Bar", "token(Foo), token(Bar)")
  }

  @Test
  def span() {
    val s = "FooBar"
    val tokens = MockLexer.analyze(s)
    assertEquals(Span(s, 0, 3), tokens.next().span)
    assertEquals(Span(s, 3, 6), tokens.next().span)
  }

  @Test
  def gapSpan() {
    val s = "Foo Bar"
    val tokens = MockLexer.analyze(s)
    assertEquals(Span(s, 0, 3), tokens.next().span)
    assertEquals(Span(s, 4, 7), tokens.next().span)
  }

  def assertTokens(input: String, expectation: String) {
    assertEquals(expectation, MockLexer.analyze(input).map(_.toCompleteString).mkString(", "))
  }
}