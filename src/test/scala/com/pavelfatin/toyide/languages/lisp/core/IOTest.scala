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

package com.pavelfatin.toyide.languages.lisp.core

import java.io.{File, FileWriter}

import com.pavelfatin.toyide.languages.lisp.library.LibraryTestBase
import org.junit.Assert._
import org.junit.Test

import scala.io.Source

class IOTest extends LibraryTestBase {
  @Test
  def exists() {
    withTempFile { file =>
      assertValue("(exists? \"" + format(file.getPath) + "\")", "true")
      file.delete()
      assertValue("(exists? \"" + format(file.getPath) + "\")", "false")
    }
  }

  @Test
  def directory() {
    withTempFile { file =>
      assertValue("(directory? \"" + format(file.getPath) + "\")", "false")
      assertValue("(directory? \"" + format(file.getParent) + "\")", "true")
    }
  }

  @Test
  def read() {
    withTempFile { file =>
      write(file, "data")
      assertValue("(read (open \"" + format(file.getPath) + "\"))", "(\\d \\a \\t \\a)")
    }
  }

  @Test
  def write() {
    withTempFile { file =>
      run("(let [file (open \"" + format(file.getPath) + "\")] (write file \"data\") (close file))")
      assertEquals("data", read(file))
    }
  }

  private def format(path: String) = path.replace('\\', '/')

  private def withTempFile(f: File => Unit) {
    val file = File.createTempFile("toyide", "test")
    file.deleteOnExit()
    try {
      f(file)
    } finally {
      file.delete()
    }
  }

  private def write(file: File, s: String) {
    val writer = new FileWriter(file)
    writer.write(s)
    writer.close()
  }

  private def read(file: File) = Source.fromFile(file).mkString
}