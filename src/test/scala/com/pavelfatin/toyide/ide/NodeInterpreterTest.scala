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