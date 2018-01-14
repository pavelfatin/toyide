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

package com.pavelfatin.toyide.languages.toy

import com.pavelfatin.toyide.node.NodeType

abstract sealed class ToyType(presentation: String) extends NodeType(presentation)

object ToyType {
  case object StringType extends ToyType("string")

  case object IntegerType extends ToyType("integer")

  case object BooleanType extends ToyType("boolean")

  case object VoidType extends ToyType("void")
}