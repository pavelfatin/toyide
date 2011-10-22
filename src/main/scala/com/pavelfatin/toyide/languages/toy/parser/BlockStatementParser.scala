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

import com.pavelfatin.toyide.parser.{TreeBuilder, Parser}
import com.pavelfatin.toyide.languages.toy.ToyTokens._

object BlockStatementParser extends Parser {
  def parse(in: TreeBuilder) {
    if(in.matches(VAR)) {
      VariableParser.parse(in)
      return
    }

    if(in.matches(RETURN)) {
      ReturnParser.parse(in)
      return
    }

    if(in.matches(WHILE)) {
      WhileParser.parse(in)
      return
    }

    if(in.matches(IF)) {
      IfParser.parse(in)
      return
    }

    if(in.matches(IDENT)) {
      if(in.ahead(LPAREN)) {
        CallParser.parse(in)
        return
      }

      if(in.ahead(EQ)) {
        AssignmentParser.parse(in)
        return
      }
    }

    if(in.matches(COMMENT)) {
      CommentParser.parse(in)
      return
    }

    if(in.matches(SEMI)) {
      EmptyParser.parse(in)
      return
    }

    in.error("Wrong statement")

    if(!in.isEOF) in.advance()
  }
}