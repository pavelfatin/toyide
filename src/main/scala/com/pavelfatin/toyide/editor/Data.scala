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

import com.pavelfatin.toyide.node.Node
import com.pavelfatin.toyide.lexer.Token
import com.pavelfatin.toyide.{Interval, ObservableEvents}
import com.pavelfatin.toyide.inspection.Decoration

trait Data extends ObservableEvents[DataEvent] {
  def text: String

  def tokens: Seq[Token]

  def structure: Option[Node]

  def errors: Seq[Error]

  def hasFatalErrors: Boolean

  def pass: Pass

  def hasNextPass: Boolean

  def nextPass()

  def compute()
}

case class DataEvent(pass: Pass, errors: Seq[Error])

case class Error(interval: Interval, message: String, decoration: Decoration = Decoration.Underline, fatal: Boolean = true)

sealed abstract class Pass(val next: Option[Pass])

object Pass {
  case object Text extends Pass(Some(Lexer))

  case object Lexer extends Pass(Some(Parser))

  case object Parser extends Pass(Some(Inspections))

  case object Inspections extends Pass(None)
}