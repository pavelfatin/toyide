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

package com.pavelfatin.toyide.compiler

import jasmin.ClassFile
import java.io.{ByteArrayOutputStream, StringReader}
import com.pavelfatin.toyide.node.Node

object Assembler {
  def assemble(root: Node, name: String): Array[Byte] = {
    val code = root.translate(name, new Labels()).toText(name)

    val input = new StringReader(code)
    val output = new ByteArrayOutputStream()

    val file = new ClassFile()
    file.readJasmin(input, name, false)
    if (file.errorCount() > 0) throw new RuntimeException("Assembling error: %s".format(code))
    file.write(output)

    val bytes = output.toByteArray

    output.close()
    input.close()

    bytes
  }
}