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

package com.pavelfatin.toyide.editor.controller

import java.awt.{Insets, Dimension}

import org.junit.Test
import org.junit.Assert._
import com.pavelfatin.toyide.Helpers._
import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.editor._
import com.pavelfatin.toyide.formatter.FormatterImpl

class ControllerImplTest {
  @Test
  def enter() {
    assertEffectIs("|", "\n|")(_.processEnterPressed())
    assertEffectIs("|foo", "\n|foo")(_.processEnterPressed())
    assertEffectIs("foo|", "foo\n|")(_.processEnterPressed())
    assertEffectIs("foo|bar", "foo\n|bar")(_.processEnterPressed())
  }

  @Test
  def enterWithCaretHold() {
    assertEffectIs("foo|bar", "foo|\nbar")(_.processEnterPressed(true))
  }

  @Test
  def enterWithIndent() {
    assertEffectIs("  foo|", "  foo\n  |")(_.processEnterPressed())
    assertEffectIs("  foo|bar", "  foo\n  |bar")(_.processEnterPressed())

    assertEffectIs("{\n  |", "{\n  \n  |")(_.processEnterPressed())
  }

  @Test
  def enterWithIndentIncrease() {
    assertEffectIs("{|", "{\n  |")(_.processEnterPressed())
    assertEffectIs("  {|", "  {\n    |")(_.processEnterPressed())

    assertEffectIs("{ |", "{ \n  |")(_.processEnterPressed())
  }

  @Test
  def enterWithIndentDecrease() {
    assertEffectIs("{|}", "{\n  |\n}")(_.processEnterPressed())
    assertEffectIs("  {|}", "  {\n    |\n  }")(_.processEnterPressed())

    assertEffectIs("{| }", "{\n | }")(_.processEnterPressed())
  }

  @Test
  def enterWithIndentPersists() {
    assertEffectIs("|{", "\n|{")(_.processEnterPressed())
    assertEffectIs("  |{", "  \n  |{")(_.processEnterPressed())

    assertEffectIs("}|", "}\n|")(_.processEnterPressed())
    assertEffectIs("  }|", "  }\n  |")(_.processEnterPressed())

    assertEffectIs("{}|", "{}\n|")(_.processEnterPressed())
    assertEffectIs("  {}|", "  {}\n  |")(_.processEnterPressed())

    assertEffectIs("|{}", "\n|{}")(_.processEnterPressed())
    assertEffectIs("  |{}", "  \n  |{}")(_.processEnterPressed())

    assertEffectIs("{\n  |}", "{\n  \n  |}")(_.processEnterPressed())

    assertEffectIs("  foo\n|  bar", "  foo\n\n|  bar")(_.processEnterPressed())
    assertEffectIs("  foo\n | bar", "  foo\n \n | bar")(_.processEnterPressed())
    assertEffectIs("  foo\n  |bar", "  foo\n  \n  |bar")(_.processEnterPressed())
  }

  @Test
  def enterBeforePureIndent() {
    assertEffectIs("|  ", "\n|  ")(_.processEnterPressed())
  }

  @Test
  def char() {
    assertEffectIs("|", "a|")(_.processCharInsertion('a'))
    assertEffectIs("a|c", "ab|c")(_.processCharInsertion('b'))
  }

  @Test
  def charComplement() {
    assertEffectIs("|", "(|)")(_.processCharInsertion('('))
    assertEffectIs("|", "[|]")(_.processCharInsertion('['))
    assertEffectIs("|", "{|}")(_.processCharInsertion('{'))
    assertEffectIs("|", "\"|\"")(_.processCharInsertion('"'))
  }

  @Test
  def charComplementSuppression() {
    assertEffectIs("|a", "(|a")(_.processCharInsertion('('))
    assertEffectIs("|1", "(|1")(_.processCharInsertion('('))
    assertEffectIs("|(", "(|(")(_.processCharInsertion('('))
  }

  @Test
  def charComplementNonSuppression() {
    assertEffectIs("|)", "[|])")(_.processCharInsertion('['))
    assertEffectIs("|)", "(|))")(_.processCharInsertion('('))
  }

  @Test
  def charComplementOverwrite() {
    assertEffectIs("(|)", "()|")(_.processCharInsertion(')'))
  }

  @Test
  def charClosingMark() {
    assertEffectIs("|", "}|")(_.processCharInsertion('}'))
  }

  @Test
  def charClosingMarkIndentDecrease() {
    assertEffectIs("  foo\n  |", "  foo\n}|")(_.processCharInsertion('}'))
    assertEffectIs("    foo\n    |", "    foo\n  }|")(_.processCharInsertion('}'))
  }

  @Test
  def charClosingMarkIndentDecreaseAfterIncrease() {
    assertEffectIs("  {\n  |", "  {\n  }|")(_.processCharInsertion('}'))
  }

  @Test
  def charClosingMarkIndentDecreaseBlocked() {
    assertEffectIs("    foo\n   a|", "    foo\n   a}|")(_.processCharInsertion('}'))
  }

  @Test
  def charClosingMarkIndentIncrease() {
    assertEffectIs("  foo\n|", "  foo\n}|")(_.processCharInsertion('}'))
    assertEffectIs("    foo\n|", "    foo\n  }|")(_.processCharInsertion('}'))
  }

  @Test
  def charClosingMarkIndentIncreaseBlocked() {
    assertEffectIs("    foo\na|", "    foo\na}|")(_.processCharInsertion('}'))
  }

  protected def assertEffectIs(before: String, after: String)(f: ControllerImpl => Unit) {
    doAssertEffectIs(before, after) { (document, terminal) =>
      val GridMock = new Grid(new Dimension(8, 8), new Insets(0, 0, 0, 0))
      val controller = new ControllerImpl(document, new DataMock(), terminal, GridMock, new AdviserMock(),
        new FormatterImpl(new MockFormat()), 2, "//", new HistoryImpl())
      f(controller)
    }
  }

  private def doAssertEffectIs(before: String, after: String)(block: (Document, Terminal) => Unit) {
    val (document, terminal) = parseDocument(before)
    block(document, terminal)
    val text = formatDocument(document, terminal)
    assertEquals(after, text)
  }
}