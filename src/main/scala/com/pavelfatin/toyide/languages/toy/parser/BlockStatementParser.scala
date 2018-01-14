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