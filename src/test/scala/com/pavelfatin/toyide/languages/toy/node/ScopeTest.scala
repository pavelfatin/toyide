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

package com.pavelfatin.toyide.languages.toy.node

import com.pavelfatin.toyide.languages.toy.parser.ProgramParser
import com.pavelfatin.toyide.languages.toy.ToyLexer
import com.pavelfatin.toyide.Helpers
import org.junit.Test
import org.junit.Assert._

class ScopeTest {
  @Test
  def empty() {
    assertEquals(None, exitIn(""))
  }

  @Test
  def statements() {
    assertEquals(None, exitIn("""
      var v: integer = 0;
      v = 1;
      def f(p: integer): void = {}
      f(2);
      while (true) {}
      if (true) {} else {}
      // comment
    """))
  }

  @Test
  def returnStatement() {
    assertEquals(Some(0), exitIn("return;"))
  }

  @Test
  def severalExits() {
    assertEquals(Some(1), exitIn("""
    var a: integer = 1;
    return;
    var b: integer = 2;
    return;
    """))
  }

  @Test
  def returnInsideHolders() {
    assertEquals(None, exitIn("while (false) { return; }"))
    assertEquals(None, exitIn("def f(): void = { return; }"))
  }

  @Test
  def ifWithElse() {
    assertEquals(None, exitIn("if (false) { return; }"))
    assertEquals(None, exitIn("if (false) {} else { return; }"))
    assertEquals(None, exitIn("if (false) { if (false) { return; } else {} } else { return; }"))
    assertEquals(None, exitIn("if (false) { return; } else { if (false) { return; } else {} }"))

    assertEquals(Some(0), exitIn("""
      if (false) {
        return;
      } else {
        return;
      }
      """))

    assertEquals(Some(0), exitIn("""
      if (false) {
        if (false) {
          return;
        } else {
          return;
        }
      } else {
        return;
      }
      """))

    assertEquals(Some(0), exitIn("""
      if (false) {
        return;
      } else {
        if (false) {
          return;
        } else {
          return;
        }
      }
      """))
  }

  protected def exitIn(code: String): Option[Int] = {
    val root = ProgramParser.parse(ToyLexer.analyze(code))
    val elements = root.elements
    Helpers.assertNoProblemsIn(elements)
    Helpers.assertNoUnresolvedIn(elements)
    val exit = root.asInstanceOf[Scope].exit
    exit.map(it => root.span.text.take(it.span.begin).count(_ == '\n'))
  }
}