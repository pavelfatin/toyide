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

package com.pavelfatin.toyide.languages.lisp.node

import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.compiler.{TranslationException, Labels}
import com.pavelfatin.toyide.interpreter.Context
import com.pavelfatin.toyide.languages.lisp.library.Library
import com.pavelfatin.toyide.languages.lisp.value._
import com.pavelfatin.toyide.node.NodeImpl

class ProgramNode extends NodeImpl("program") {
  def expressions: Seq[ExpressionNode] = children.filterBy[ExpressionNode]

  override def evaluate(context: Context, output: Output) = {
    val environment = Library.instance.createEnvironment()
    val value = evaluate(ProgramNode.Source, environment, output)
    Some(value)
  }

  def evaluate(source: String, environment: Environment, output: Output): Expression = {
    val values = expressions.map(_.read(source))
    values.foldLeft[Expression](ListValue.Empty)((_, x) => x.eval(environment, output))
  }

  override def translate(className: String, labels: Labels) =
    throw new TranslationException("Translation to bytecode is not yet implemented.")
}

object ProgramNode {
  private val Source = "User"
}