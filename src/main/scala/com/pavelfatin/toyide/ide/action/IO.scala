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