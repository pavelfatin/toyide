/*
 * Copyright (C) 2014 Pavel Fatin <http://pavelfatin.com>
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

package com.pavelfatin.toyide.languages.lisp

import com.pavelfatin.toyide.Example
import com.pavelfatin.toyide.languages.lisp.library.Library

object LispExamples {
  val WebServerCode = load("/examples/WebServer.lisp")

  private def load(file: String): String = {
    val source = io.Source.fromURL(getClass.getResource(file))
    val code = source.getLines().mkString("\n")
    source.close()
    code
  }

  val Values = Seq(
    Example("Web Server", 'W', WebServerCode),
    Example("Core Library", 'C', Library.CoreCode),
    Example("Function Library", 'F', Library.FunctionCode),
    Example("Arithmetic Library", 'A', Library.ArithmeticCode),
    Example("List Library", 'L', Library.ListCode))
}
