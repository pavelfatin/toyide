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