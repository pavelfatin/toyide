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

package com.pavelfatin.toyide.ide

import com.pavelfatin.toyide.editor.Runner
import com.pavelfatin.toyide.node.Node
import com.pavelfatin.toyide.interpreter.{EvaluationException, ContextImpl}
import java.awt.Color

private class NodeInterpreter(console: Console) extends Runner {
  private val ErrorColor = new Color(127, 0, 0)

  def run(root: Node) {
    console.clear()
    console.print("Started:\n")
    val before = System.currentTimeMillis
    try {
      root.evaluate(new ContextImpl(), console)
      val elapsed = System.currentTimeMillis - before
      console.print("\nFinished (%d ms)".format(elapsed))
    } catch {
      case EvaluationException(message, trace) =>
        console.print("\nError: %s".format(message), ErrorColor)
        trace.foreach { place =>
          val line = place.line + 1
          console.print("\n  at ", ErrorColor)
          console.printLink("%s%d".format(place.enclosure.map(_.formatted("%s:")).mkString, line), line)
        }
    }
  }
}
