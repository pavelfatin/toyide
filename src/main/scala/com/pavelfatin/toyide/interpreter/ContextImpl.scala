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

package com.pavelfatin.toyide.interpreter

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class ContextImpl extends Context {
  private val MaxFrames = 100

  private var frames = List[Frame]()

  private var allocations = List[ListBuffer[String]]()

  private val heap = mutable.Map[String, Value]()

  def get(local: Boolean, name: String): Value = {
    storage(local).getOrElse(name,
      throw new IllegalStateException(
        "%s value not found: %s".format(place(local), name)))
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
    val storage = frames.headOption.fold(heap)(_.values)
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

  private case class Frame(place: Place, values: mutable.Map[String, Value] = mutable.Map())

  private case class ReturnException(value: Option[Value]) extends RuntimeException
}