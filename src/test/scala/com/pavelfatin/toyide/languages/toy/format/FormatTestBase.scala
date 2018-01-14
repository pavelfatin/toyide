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