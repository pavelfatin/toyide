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

package com.pavelfatin.toyide.languages.toy.interpreter

import com.pavelfatin.toyide.interpreter.DelegateValue
import com.pavelfatin.toyide.languages.toy.ToyType

sealed trait ToyValue[T] extends DelegateValue[T]

object ToyValue {
  case class StringValue(content: String) extends ToyValue[String] {
    def valueType = ToyType.StringType
  }

  case class IntegerValue(content: Int) extends ToyValue[Int] {
    def valueType = ToyType.IntegerType
  }

  case class BooleanValue(content: Boolean) extends ToyValue[Boolean] {
    def valueType = ToyType.BooleanType
  }
}