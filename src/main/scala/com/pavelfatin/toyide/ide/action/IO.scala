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

import java.io.{FileWriter, BufferedWriter, File}
import io.Source

private object IO {
  def write(file: File, text: String) {
    val s = text.replaceAll("\n", System.getProperty("line.separator"))
    val writer = new BufferedWriter(new FileWriter(file))
    try {
      writer.write(s)
      writer.flush()
    } finally {
      writer.close()
    }
  }

  def read(file: File): String = {
    val source = Source.fromFile(file)
    try {
      source.getLines().mkString("\n")
    } finally {
      source.close()
    }
  }
}