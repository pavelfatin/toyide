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