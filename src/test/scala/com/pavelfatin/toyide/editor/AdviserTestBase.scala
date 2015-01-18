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
    val anchor = root.elements.find(it => it.isLeaf && it.span.text.contains(label)).lastOption
    anchor match {
      case Some(it) =>
        val variants = adviser.variants(root, it).map(_.title)
        assertTrue("Actual variants: " + variants.mkString(", "), check(variants))
      case None =>
        fail("No anchor found: %s".format(code))
    }
  }
}
