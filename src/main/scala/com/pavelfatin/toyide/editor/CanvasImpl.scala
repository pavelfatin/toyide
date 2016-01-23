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

import java.awt.event._
import java.awt.{Dimension, Rectangle}
import javax.swing.{JScrollPane, JComponent}

private class CanvasImpl(component: JComponent, scrollPane: JScrollPane) extends Canvas {
  private var _caretVisible = false

  component.addHierarchyListener(new HierarchyListener {
    override def hierarchyChanged(e: HierarchyEvent): Unit = {
      if ((e.getChangeFlags & HierarchyEvent.SHOWING_CHANGED) > 0) {
        notifyObservers(VisibilityChanged(component.isShowing))
      }
    }
  })

  private val scrollListener = new AdjustmentListener {
    def adjustmentValueChanged(e: AdjustmentEvent) {
      notifyObservers(VisibleRectangleChanged(component.getVisibleRect))
    }
  }

  scrollPane.getVerticalScrollBar.addAdjustmentListener(scrollListener)
  scrollPane.getHorizontalScrollBar.addAdjustmentListener(scrollListener)

  component.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) {
      notifyObservers(VisibleRectangleChanged(component.getVisibleRect))
    }
  })

  component.addFocusListener(new FocusListener {
    def focusGained(e: FocusEvent) {
      notifyObservers(FocusChanged(true))
    }

    def focusLost(e: FocusEvent) {
      notifyObservers(FocusChanged(false))
    }
  })

  def size: Dimension = component.getSize

  def visible: Boolean = component.isShowing

  def visibleRectangle: Rectangle = component.getVisibleRect

  def hasFocus: Boolean = component.hasFocus

  def caretVisible = _caretVisible

  def caretVisible_=(b: Boolean) {
    if (_caretVisible != b) {
      _caretVisible = b
      notifyObservers(CaretVisibilityChanged(b))
    }
  }
}
