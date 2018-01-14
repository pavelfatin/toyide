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

package com.pavelfatin.toyide.languages.toy.parser

import org.junit.Assert._
import com.pavelfatin.toyide.languages.toy.ToyLexer
import com.pavelfatin.toyide.parser.Parser
import com.pavelfatin.toyide.lexer.Lexer

abstract class ParserTest(parser: Parser, lexer: Lexer = ToyLexer) {
  def parsed(s: String): String =
    parser.parse(lexer.analyze(s)).content

  def assertParsed(s: String, expectation: String) {
    assertEquals(format(expectation), parsed(s).trim)
  }

  private def format(expectation: String) = {
    val content = expectation.replace("\r\n", "\n")
      .replaceFirst("^ *\n", "").replaceFirst("\\s+$", "")

    val indent = content.takeWhile(_ == ' ').size

    content.split('\n').map(_.drop(indent)).mkString("\n")
  }
}