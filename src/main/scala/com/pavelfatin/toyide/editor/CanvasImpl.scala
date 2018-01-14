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
