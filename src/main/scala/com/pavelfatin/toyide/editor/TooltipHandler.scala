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

import java.awt.{Dimension, Point, Graphics, Color}
import java.awt.event.{MouseEvent, MouseAdapter, ActionEvent, ActionListener}
import javax.swing._
import javax.swing.border.{EmptyBorder, CompoundBorder, LineBorder}

private class TooltipHandler(component: JComponent, lookup: Point => Option[Error]) {
  private val Timeout = 500

  private val TooltipShift = new Dimension(5, 5)

  private val TooltipBackground = new Color(0xFDFEE2)

  private val TooltipBorder = new CompoundBorder(
    new LineBorder(Color.BLACK, 1, true),
    new EmptyBorder(3, 3, 3, 3))

  private var pointer: Option[Point] = None

  private var popup: Option[Popup] = None

  private val tooltipTimer = new Timer(Timeout, new ActionListener() {
    def actionPerformed(e: ActionEvent) {
      for (point <- pointer; error <- lookup(point)) {
        val p = createPopup(error, new Point(point.x + TooltipShift.width, point.y + TooltipShift.height))
        popup = Some(p)
        p.show()
      }
    }
  })

  tooltipTimer.setRepeats(false)

  component.addMouseMotionListener(new MouseAdapter() {
    override def mouseMoved(e: MouseEvent) {
      val p = e.getPoint;

      for (point <- pointer; if p.distance(point.x, point.y) < 7)
        return

      popup.foreach(it => it.hide())
      pointer = Some(p)
      tooltipTimer.restart()
    }
  })

  component.addMouseListener(new MouseAdapter() {
    override def mouseExited(e: MouseEvent) {
      tooltipTimer.stop()
    }
  })

  private def createPopup(error: Error, point: Point): Popup = {
    val factory = PopupFactory.getSharedInstance
    val shift = component.getLocationOnScreen
    val label = createLabel(error)
    label.setBorder(TooltipBorder)
    factory.getPopup(component, label, shift.x + point.x, shift.y + point.y)
  }

  private def createLabel(error: Error): JLabel = {
    new JLabel(error.message) {
      override def paint(g: Graphics) {
        g.setColor(TooltipBackground)
        g.fillRect(0, 0, getWidth, getHeight)
        super.paint(g)
      }
    }
  }

  def dispose() {
    tooltipTimer.stop()
  }
}