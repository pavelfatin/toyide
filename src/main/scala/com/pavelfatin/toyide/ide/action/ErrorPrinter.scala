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