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