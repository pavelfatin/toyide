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

package com.pavelfatin.toyide.languages.toy.format

import org.junit.Assert._
import com.pavelfatin.toyide.parser.Parser
import com.pavelfatin.toyide.languages.toy.{ToyLexer, ToyFormat}
import com.pavelfatin.toyide.Helpers._
import com.pavelfatin.toyide.formatter.FormatterImpl

class FormatTestBase {
  private val formatter = new FormatterImpl(ToyFormat)

  protected def assertFormatted(code: String, parser: Parser, expectation: String, check: Boolean = true) {
    val node = parser.parse(ToyLexer.analyze(code))
    if(check) assertNoProblemsIn(node.elements)
    val actual = formatter.format(node, None, 2)
    assertEquals(expectation, actual)
  }
}