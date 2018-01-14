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

package com.pavelfatin.toyide.languages.toy.compiler

import org.junit.Assert._
import com.pavelfatin.toyide.Helpers._
import com.pavelfatin.toyide.languages.toy.parser.ProgramParser
import com.pavelfatin.toyide.languages.toy.ToyLexer
import com.pavelfatin.toyide.MockConsole
import com.pavelfatin.toyide.compiler.{Labels, BytecodeInvoker, Assembler}

trait TranslatorTesting {
  private val Name = "Main"

  protected def assertOutput(code: String, expected: String) {
    assertEquals(expected, run(code))
  }

  protected def run(code: String): String = {
    val root = ProgramParser.parse(ToyLexer.analyze(code))

    val elements = root.elements
    assertNoProblemsIn(elements)
    assertNoUnresolvedIn(elements)

    val bytecode = Assembler.assemble(root, Name)

    val output = new MockConsole()
    BytecodeInvoker.invoke(bytecode, Name, output)
    output.text
  }
}