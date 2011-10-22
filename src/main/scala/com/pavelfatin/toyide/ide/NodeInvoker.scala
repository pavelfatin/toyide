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

package com.pavelfatin.toyide.ide

import com.pavelfatin.toyide.editor.Runner
import com.pavelfatin.toyide.node.Node
import com.pavelfatin.toyide.compiler._
import java.awt.Color

private class NodeInvoker(console: Console) extends Runner {
  private val ErrorColor = new Color(127, 0, 0)

  private val Name = "Main"

  def run(root: Node) {
    console.clear()
    try {
      val bytecode = Assembler.assemble(root, Name)
      console.print("Started:\n")
      val before = System.currentTimeMillis
      try {
        BytecodeInvoker.invoke(bytecode, Name, console)
        val elapsed = System.currentTimeMillis - before
        console.print("\nFinished (%d ms)".format(elapsed))
      } catch {
        case InvocationException(message, trace) =>
          if (message != "java.lang.ThreadDeath") {
            console.print("\n%s".format(message), ErrorColor)
            trace.foreach { place =>
              val line = place.line + 1
              console.print("\n  at ", ErrorColor)
              console.printLink("%s%d".format(place.enclosure.map(_.formatted("%s:")).mkString, line), line)
            }
          }
      }
    } catch {
      case TranslationException(message) => console.print("Compilation error.\n%s".format(message), ErrorColor)
    }
  }
}
