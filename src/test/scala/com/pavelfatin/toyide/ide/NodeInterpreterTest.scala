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

package com.pavelfatin.toyide.ide

import com.pavelfatin.toyide.MockConsole
import com.pavelfatin.toyide.languages.toy.parser.ProgramParser
import com.pavelfatin.toyide.languages.toy.{ToyLexer, ToyExamples}
import org.junit.{Assert, Test}

class NodeInterpreterTest {
  @Test
  def output() {
    Assert.assertEquals("""Started:
233168
Finished (n ms)""".filter(_ != '\r'), run(ToyExamples.Euler1).replaceFirst("\\d+ ms", "n ms"))
  }

  @Test
  def exception() {
    Assert.assertEquals("""Started:

Error: Division by zero
  at c:5
  at b:9
  at a:13
  at 16""".filter(_ != '\r'), run(ToyExamples.Exception))
  }

  private def run(code: String): String = {
    val console = new MockConsole()
    val interpreter = new NodeInterpreter(console)
    interpreter.run(ProgramParser.parse(ToyLexer.analyze(code)))
    val text = console.text
    text
  }
}