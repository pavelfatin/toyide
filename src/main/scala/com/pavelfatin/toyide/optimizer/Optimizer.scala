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

package com.pavelfatin.toyide.optimizer

import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.node.Node

object Optimizer {
  def optimize(root: Node, document: Document) {
    optimizationsIn(root).reverse.foreach(p => document.replace(p._1.span.interval, p._2))
  }

  private def optimizationsIn(node: Node): Seq[(Node, String)] = node.optimized match {
    case Some(s) => if (node.span.text == s) Seq.empty else Seq(node -> s)
    case None => node.children.flatMap(optimizationsIn)
  }
}