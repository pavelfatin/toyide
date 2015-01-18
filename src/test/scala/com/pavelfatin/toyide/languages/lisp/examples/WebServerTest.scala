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

package com.pavelfatin.toyide.languages.lisp.examples

import com.pavelfatin.toyide.languages.lisp.{LispExamples, InterpreterTesting}
import com.pavelfatin.toyide.languages.lisp.library.Library
import org.junit.Assert._
import org.junit.{Before, Test}

class WebServerTest extends InterpreterTesting {
  private val RequestTemplate = load("/RequestTemplate.txt")

  private val Code = LispExamples.WebServerCode
    .replace("./doc", "./license")
    .replace("(listen port handle-connection)", "(handle-connection mock-socket)")

  private val handle = new MockHandle()

  override def createEnvironment() = Library.instance.createEnvironment()
    .addLocals(Map("mock-socket" -> handle))

  @Before
  def resetHandle() {
    handle.reset()
  }

  @Test
  def normal() {
    assertResponse("/scala-license.txt", load("/NormalResponse.txt"))
  }

  @Test
  def notFound() {
    assertResponse("/unknown.html", load("/NotFoundResponse.txt"))
  }

  @Test
  def index() {
    assertResponse("/", load("/IndexResponse.txt"))
  }

  private def assertResponse(uri: String, expected: String) {
    handle.input = RequestTemplate.format(uri)
    run(Code)
    assertEquals(expected, handle.output)
    assertTrue(handle.closed)
  }

  private def load(file: String): String = {
    val source = io.Source.fromURL(getClass.getResource(file))
    val code = source.getLines().mkString("\n")
    source.close()
    code
  }
}
