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

import org.junit.Test

class AssignmentParserTest extends ParserTest(AssignmentParser) {
  @Test
  def normal() {
    assertParsed("a = 1;",
      """
      assignment
        referenceToValue
          a
        EQ
        literal
          1
        SEMI
      """)
  }

  @Test
  def noSemi() {
    assertParsed("a = 1",
      """
      assignment
        referenceToValue
          a
        EQ
        literal
          1
        error: leaf
      """)
  }

  @Test
  def noExpression() {
    assertParsed("a =",
      """
      assignment
        referenceToValue
          a
        EQ
        error: leaf
      """)
  }
//
//  @Test
//  def noAssignment = assertParsed("a",
//"""
//assignment
// a
// error: leaf
//""")
//
//  @Test
//  def empty = assertParsed("",
//"""
//assignment
// error: leaf
//""")
}