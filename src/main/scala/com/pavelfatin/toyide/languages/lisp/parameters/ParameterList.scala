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

package com.pavelfatin.toyide.languages.lisp.parameters

import com.pavelfatin.toyide.languages.lisp.value.{Expression, ListValue}

import scala.Function._

private class ParameterList(singularParameters: Seq[Parameters], pluralParameter: Option[Parameters]) extends AbstractParameters {
  protected def bind0(argument: Expression) = {
    val arguments = argument match {
      case ListValue(expressions) => expressions
      case e => error("Cannot deconstruct non-list value: " + e.presentation)
    }

    if (arguments.size < singularParameters.size) {
      error("Not enough arguments: " + Expression.format(arguments))
    }

    if (arguments.size > singularParameters.size && pluralParameter.isEmpty) {
      error("Too many arguments: " + Expression.format(arguments))
    }

    val (singularArguments, restArguments) = arguments.splitAt(singularParameters.size)

    val singularPairs = singularParameters.zip(singularArguments)
    val pluralPair = pluralParameter.map((_, ListValue(restArguments)))

    val pairs = singularPairs ++ pluralPair.toSeq

    val bindings = pairs.flatMap {
      case (pattern, initializer) => pattern.bind(initializer).fold(error, identity)
    }

    bindings.toMap
  }

  def symbols = (singularParameters ++ pluralParameter.toSeq).flatMap(_.symbols)

  def presentation = singularParameters.size + pluralParameter.map(const("*")).mkString
}
