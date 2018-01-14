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

package com.pavelfatin.toyide.editor

import com.pavelfatin.toyide.lexer.Lexer
import com.pavelfatin.toyide.parser.Parser
import org.junit.Assert._

abstract class AdviserTestBase(lexer: Lexer, parser: Parser, adviser: Adviser) {
  protected def assertVariantsAre(code: String)(expected: String*) {
    assertVariants(code, _.toList == expected.toList)
  }

  protected def assertVariantsInclude(code: String)(expected: String*) {
    assertVariants(code, variants => expected.forall(variants.contains))
  }

  protected def assertVariantsExclude(code: String)(expected: String*) {
    assertVariants(code, variants => !expected.exists(variants.contains))
  }

  private def assertVariants(code: String, check: Seq[String] => Boolean) {
    val label = Adviser.Anchor
    val s = code.replaceFirst("\\|", label)
    val root = parser.parse(lexer.analyze(s))
    val anchor = root.elements.find(it => it.isLeaf && it.span.text.contains(label))
    anchor match {
      case Some(it) =>
        val variants = adviser.variants(root, it).map(_.title)
        assertTrue("Actual variants: " + variants.mkString(", "), check(variants))
      case None =>
        fail("No anchor found: %s".format(code))
    }
  }
}
