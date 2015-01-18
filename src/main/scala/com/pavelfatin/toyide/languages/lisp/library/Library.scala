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

package com.pavelfatin.toyide.languages.lisp.library

import com.pavelfatin.toyide.ide.Console
import com.pavelfatin.toyide.languages.lisp.node.ProgramNode
import com.pavelfatin.toyide.languages.lisp.value.{Environment, EnvironmentImpl, Expression}
import com.pavelfatin.toyide.languages.lisp.{LispLexer, LispParser}
import com.pavelfatin.toyide.lexer.Token

class Library private (globals: Map[String, Expression]) {
  def createEnvironment(): Environment = new EnvironmentImpl(globals)

  def symbols: Set[String] = globals.keySet
}

object Library {
  val CoreCode = load("/library/Core.lisp")
  val FunctionCode = load("/library/Function.lisp")
  val ArithmeticCode = load("/library/Arithmetic.lisp")
  val ListCode = load("/library/List.lisp")

  private var cachedInstance: Option[Library] = None

  private var busy = false

  def instance: Library = cachedInstance.getOrElse {
    val library = exclusively(new Library(Map.empty))(createLibrary())
    cachedInstance = Some(library)
    library
  }

  private def exclusively[T](default: => T)(create: => T): T = {
    if (busy) default else {
      busy = true
      val result = create
      busy = false
      result
    }
  }

  private def createLibrary(): Library = {
    val environment = new EnvironmentImpl()

    initialize(environment, "Core", CoreCode)
    initialize(environment, "Function", FunctionCode)
    initialize(environment, "Arithmetic", ArithmeticCode)
    initialize(environment, "List", ListCode)

    new Library(environment.globals.toMap)
  }

  private def load(file: String): String = {
    val source = io.Source.fromURL(getClass.getResource(file))
    val code = source.getLines().mkString("\n")
    source.close()
    code
  }

  private def initialize(environment: Environment, source: String, code: String): Environment =  {
    val program = parse(code)
    program.evaluate(source, environment, Console.Null)
    environment
  }
  
  private def parse(code: String): ProgramNode = {
    val tokens = analyze(code)

    val program = LispParser.parse(tokens.iterator).asInstanceOf[ProgramNode]

    val parserProblems = program.elements.filter(_.problem.isDefined)
    assert(parserProblems.isEmpty, parserProblems.mkString(", "))
    
    program
  }

  private def analyze(code: String): Seq[Token] = {
    val tokens = LispLexer.analyze(code).toSeq

    val lexerProblems = tokens.filter(_.problem.isDefined)
    assert(lexerProblems.isEmpty, lexerProblems.mkString(", "))
    
    tokens
  }
}
