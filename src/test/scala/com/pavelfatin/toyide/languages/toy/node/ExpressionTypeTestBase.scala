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