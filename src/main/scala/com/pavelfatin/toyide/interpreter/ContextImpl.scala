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

package com.pavelfatin.toyide.interpreter

import collection.mutable.{ListBuffer, Map}

class ContextImpl extends Context {
  private val MaxFrames = 100

  private var frames = List[Frame]()

  private var allocations = List[ListBuffer[String]]()

  private val heap = Map[String, Value]()

  def get(local: Boolean, name: String): Value = {
    storage(local).get(name).getOrElse {
      throw new IllegalStateException(
        "%s value not found: %s".format(place(local), name))
    }
  }

  def put(local: Boolean, name: String, value: Value) {
    storage(local).put(name, value) match {
      case Some(v) =>
        throw new IllegalStateException(
          "%s value %s is already exists: %s".format(place(local), name, v.presentation))
      case None => // ok
    }
    val scope = allocations.headOption.getOrElse(throw new IllegalStateException(
      "%s value %s (%s) allocation without a scope".format(place(local), name, value.presentation)))
    scope.append(name)
  }

  def update(local: Boolean, name: String, value: Value) {
    storage(local).put(name, value) match {
      case Some(previous) =>
        if (previous.valueType != value.valueType)
          throw new IllegalStateException(
            "Type mismatch, expected %s, actual: %s".format(previous.valueType, value.valueType))
      case None =>
        throw new IllegalStateException(
          "%s value not found: %s".format(place(local), name))
    }
  }

  private def place(local: Boolean) = if (local) "Frame" else "Heap"

  private def storage(local: Boolean) = {
    if (local)
      frames.headOption.map(_.values).getOrElse(throw new IllegalStateException(
        "No active frame"))
    else
      heap
  }

  def inScope(action: => Unit) {
    allocations ::= ListBuffer()
    try {
      action
      clearAllocations()
    } catch {
      case e: ReturnException =>
        clearAllocations()
        throw e
    }
  }

  private def clearAllocations() {
    val storage = frames.headOption.map(_.values).getOrElse(heap)
    for (name <- allocations.head) {
      storage.remove(name) match {
        case Some(_) => // ok
        case None =>
          throw new IllegalStateException(
            "Allocated value %s not found".format(name))
      }
    }
    allocations = allocations.tail
  }

  def inFrame(place: Place)(action: => Unit): Option[Value] = {
    if (frames.size >= MaxFrames)
      throw new IllegalStateException("Stack overflow")

    frames ::= Frame(place)
    val result = try {
      action
      None
    } catch {
      case ReturnException(value) => value
    }
    frames = frames.tail
    result
  }

  def dropFrame(value: Option[Value]) {
    if (frames.isEmpty)
      throw new IllegalStateException("No frame to drop")

    throw new ReturnException(value)
  }

  def trace: Seq[Place] = frames.map(_.place)

  private case class Frame(place: Place, values: Map[String, Value] = Map())

  private case class ReturnException(value: Option[Value]) extends RuntimeException
}