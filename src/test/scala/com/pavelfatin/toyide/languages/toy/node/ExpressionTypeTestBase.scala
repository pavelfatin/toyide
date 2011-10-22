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

package com.pavelfatin.toyide.languages.toy.node

import com.pavelfatin.toyide.languages.toy.parser.ExpressionParser
import com.pavelfatin.toyide.languages.toy.ToyLexer
import com.pavelfatin.toyide.Helpers._
import org.junit.Assert._
import com.pavelfatin.toyide.node.{Expression, NodeType}

trait ExpressionTypeTestBase {
  protected def assertTypeIs(code: String, nodeType: NodeType) {
    assert(code, Some(nodeType))
  }

  protected def assertNoType(code: String) {
    assert(code, None)
  }

  private def assert(code: String, expectedType: Option[NodeType]) {
    val exp = ExpressionParser.parse(ToyLexer.analyze(code))
    assertNoProblemsIn(exp.elements)
    exp match {
      case typed: Expression => assertEquals(expectedType, typed.nodeType)
      case _ => fail("Not an expression: " + exp.content)
    }
  }
}