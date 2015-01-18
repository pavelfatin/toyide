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

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.interpreter.ValueType
import com.pavelfatin.toyide.languages.lisp.LispType
import com.pavelfatin.toyide.languages.lisp.LispType._
import com.pavelfatin.toyide.languages.lisp.value._

class TypeCheck(symbol: String, expectedType: LispType) extends CoreFunction(symbol) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(ValueType(t)) => BooleanValue(t == expectedType)
    case _ => expected("expr", arguments, environment)
  }
}

object IsInteger extends TypeCheck("integer?", IntegerType)

object IsBoolean extends TypeCheck("boolean?", BooleanType)

object IsCharacter extends TypeCheck("character?", CharacterType)

object IsSymbol extends TypeCheck("symbol?", SymbolType)

object IsFunction extends TypeCheck("function?", FunctionType)

object IsList extends TypeCheck("list?", ListType)
