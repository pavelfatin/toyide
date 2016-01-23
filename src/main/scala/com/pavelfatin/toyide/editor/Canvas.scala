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

package com.pavelfatin.toyide.editor

import java.awt.{Dimension, Rectangle}

import com.pavelfatin.toyide.ObservableEvents

trait Canvas extends ObservableEvents[CanvasEvent] {
  def size: Dimension

  def visible: Boolean

  def visibleRectangle: Rectangle

  def hasFocus: Boolean

  def caretVisible: Boolean
}

sealed trait CanvasEvent

case class VisibilityChanged(b: Boolean) extends CanvasEvent

case class VisibleRectangleChanged(r: Rectangle) extends CanvasEvent

case class FocusChanged(b: Boolean) extends CanvasEvent

case class CaretVisibilityChanged(b: Boolean) extends CanvasEvent
