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