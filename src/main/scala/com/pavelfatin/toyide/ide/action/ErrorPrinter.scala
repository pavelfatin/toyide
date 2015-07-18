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

package com.pavelfatin.toyide.ide.action

import com.pavelfatin.toyide.editor.Data
import com.pavelfatin.toyide.ide.Console
import java.awt.Color

private object ErrorPrinter {
  private val ErrorColor = new Color(127, 0, 0)

  def print(data: Data, console: Console) {
    console.clear()
    console.print("Errors found:\n", ErrorColor)
    data.errors.filter(_.fatal).foreach { it =>
      val line = data.text.substring(0, it.interval.begin).count(_ == '\n') + 1
      console.print("\nError (", ErrorColor)
      console.printLink(line.toString, line)
      console.print("): %s".format(it.message), ErrorColor)
    }
  }
}