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