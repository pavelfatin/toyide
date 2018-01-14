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

import javax.swing.border.LineBorder
import java.awt.{Point, Font, Color}
import javax.swing._
import java.awt.event._

private object ChooserFactory {
  def createPopup[T <: AnyRef](parent: JComponent, point: Point, font: Font, variants: Seq[T], renderer: ListCellRenderer[AnyRef])
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