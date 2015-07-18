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

import javax.swing.border.LineBorder
import java.awt.{Point, Font, Color}
import javax.swing._
import java.awt.event._

private object ChooserFactory {
  def createPopup[T <: AnyRef](parent: JComponent, point: Point, font: Font, variants: Seq[T], renderer: ListCellRenderer)
                              (callback: Option[T] => Unit) = {
    val list = createList(variants, font)

    list.setCellRenderer(renderer)

    val pane = new JScrollPane(list)
    pane.setBorder(new LineBorder(Color.LIGHT_GRAY))

    val factory = PopupFactory.getSharedInstance
    val shift = parent.getLocationOnScreen
    val popup = factory.getPopup(parent, pane, shift.x + point.x, shift.y + point.y)

    list.addFocusListener(new FocusAdapter() {
      override def focusLost(e: FocusEvent) {
        callback(None)
        popup.hide()
      }
    })

    list.addKeyListener(new KeyAdapter() {
      override def keyTyped(e: KeyEvent) {
        if (e.getKeyChar == KeyEvent.VK_ESCAPE) {
          callback(None)
          popup.hide()
        }
        if (e.getKeyChar == KeyEvent.VK_ENTER) {
          callback(Some(list.getSelectedValue.asInstanceOf[T]))
          popup.hide()
        }
      }
    })

    (popup, list)
  }

  private def createList(variants: Seq[AnyRef], font: Font) = {
    val list = new JList(variants.toArray[AnyRef])
    list.setBackground(new Color(235, 244, 254))
    list.setSelectionBackground(new Color(0, 82, 164))
    list.setFont(font)
    list.setSelectedIndex(0)
    list.setVisibleRowCount(variants.size min 10)

    val next = list.getActionMap.get("selectNextRow")
    val previous = list.getActionMap.get("selectPreviousRow")
    val first = list.getActionMap.get("selectFirstRow")
    val last = list.getActionMap.get("selectLastRow")

    list.getActionMap.put("selectPreviousRow", new AbstractAction() {
      def actionPerformed(e: ActionEvent) {
        val action = if (list.getSelectedIndex == 0) last else previous
        action.actionPerformed(e)
      }
    })

    list.getActionMap.put("selectNextRow", new AbstractAction() {
      def actionPerformed(e: ActionEvent) {
        val action = if (list.getSelectedIndex == list.getModel.getSize - 1) first else next
        action.actionPerformed(e)
      }
    })

    list
  }
}