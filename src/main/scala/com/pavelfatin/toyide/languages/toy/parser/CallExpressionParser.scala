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
import com.pavelfatin.toyide.languages.toy.node._

object CallExpressionParser extends Parser {
  def parse(in: TreeBuilder) {
    in.capturing(new CallExpression()) {
      in.capturing(new ReferenceToFunction()) {
        in.consume(IDENT)
      }
      ArgumentsParser.parse(in)
    }
  }
}