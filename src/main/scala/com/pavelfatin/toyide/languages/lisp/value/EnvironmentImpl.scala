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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.interpreter.{EvaluationException, Place}

import scala.collection.mutable

case class EnvironmentImpl private (locals: Map[String, Expression],
                                    globals: mutable.Map[String, Expression],
                                    ids: Iterator[Int],
                                    trace: List[Place]) extends Environment {
  def this(globals: Map[String, Expression]) {
    this(Map.empty, mutable.Map(globals.toSeq: _*), Iterator.from(0), List.empty)
  }

  def this() {
    this(Map.empty)
  }

  def lookup(name: String) = locals.get(name).orElse(globals.get(name))

  def addLocals(values: Map[String, Expression]) = copy(locals = locals ++ values)

  def clearLocals = copy(locals = Map.empty)

  def setGlobal(name: String, value: Expression) {
    globals(name) = value
  }

  def nextId() = ids.next()

  def inFrame(place: Option[Place]) = if (trace.size < EnvironmentImpl.MaxFrames) {
    copy(trace = place.getOrElse(Place(Some("Unknown"), -1)) :: trace)
  } else {
    throw new EvaluationException("Stack overflow", trace)
  }

  def interrupt(message: String, place: Option[Place] = None) =
    throw new EvaluationException(message, place.toSeq ++ trace)
}

object EnvironmentImpl {
  val MaxFrames = 200
}