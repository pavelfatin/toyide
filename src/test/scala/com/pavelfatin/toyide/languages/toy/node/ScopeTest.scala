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