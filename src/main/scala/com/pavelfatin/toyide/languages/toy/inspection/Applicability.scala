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

package com.pavelfatin.toyide.languages.toy.inspection

import com.pavelfatin.toyide.node._
import com.pavelfatin.toyide.languages.toy.node._
import com.pavelfatin.toyide.inspection.{Mark, Inspection}
import com.pavelfatin.toyide.languages.toy.ToyType._

object Applicability extends Inspection {
  val Mismatch = "Type mismatch, expected: %s, actual: %s".format(_: String, _: String)
  val Missed = "Function %s: unspecified parameters: %s".format(_: String, _: String)
  val Excessive = "Function %s: excessive argument".format(_: String)
  val Void = "Void argument"

  def inspect(node: Node): Seq[Mark] = node match {
    case call: CallExpression =>
      call.reference.flatMap { ref =>
        if (ref.predefined)
          Some(inspectPredefined(call))
        else
          call.function.map(inspect(call, _))
      } getOrElse {
        Seq.empty
      }
    case _ => Seq.empty
 }

  private def inspectPredefined(call: CallExpression): Seq[Mark] =
    call.expressions.filter(_.nodeType.contains(VoidType)).map(Mark(_, Void))

  private def inspect(call: CallExpression, function: FunctionDeclaration): Seq[Mark] = {
    val (bindings, excessive, missed) = call.bindings

    val mismatches = bindings.collect {
      case (exp @ Expression(actual), TypedNode(expected)) if actual != expected =>
        Mark(exp, Mismatch(expected.presentation, actual.presentation))
    }

    val missedMark = call.rightBrace.flatMap { brace =>
      if(missed.isEmpty) None else
        Some(Mark(brace, Missed(function.name, missed.flatMap(_.id).map(_.span.text).mkString(", "))))
    }

    val excessives = excessive.map(Mark(_, Excessive(function.name)))

    mismatches ++ excessives ++ missedMark.toSeq
  }
}