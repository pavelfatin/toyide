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

package com.pavelfatin.toyide.editor.controller

import com.pavelfatin.toyide.node.{IdentifiedNode, Node}
import com.pavelfatin.toyide.editor.{AnAction, Terminal, Data}

private class GotoDeclaration(terminal: Terminal, data: Data) extends AnAction {
  def keys = List("ctrl pressed B")

  def apply() {
    data.compute()
    for (reference <- data.referenceAt(terminal.offset);
         target <- reference.target) {
      terminal.offset = offsetOf(target)
    }
  }

  private def offsetOf(target: Node): Int = {
    target match {
      case IdentifiedNode(id, _) => id.span.begin
      case node => node.span.begin
    }
  }
}