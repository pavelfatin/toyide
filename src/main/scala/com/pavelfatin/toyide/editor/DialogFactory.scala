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

import javax.swing._
import border.EmptyBorder
import java.awt.{FlowLayout, BorderLayout, Frame}
import java.awt.event.{KeyEvent, ActionEvent, WindowEvent, WindowAdapter}

private object DialogFactory {
  def create(owner: JComponent, text: String, title: String)(callback: Option[String] => Unit): JDialog = {
    val dialog = new JDialog(owner.getTopLevelAncestor.asInstanceOf[Frame], title)

    val field = new JTextField(text)
    field.setColumns(20)
    field.setCaretPosition(text.length)
    field.selectAll()

    var done = false

    object OkAction extends AbstractAction("OK") {
      def actionPerformed(e: ActionEvent) {
        done = true
        dialog.dispose()
        callback(Some(field.getText))
      }
    }

    object CancelAction extends AbstractAction("Cancel") {
      def actionPerformed(e: ActionEvent) {
        done = true
        dialog.dispose()
        callback(None)
      }
    }

    val ok = new JButton(OkAction)
    val cancel = new JButton(CancelAction)

    val buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0))
    buttons.add(ok)
    buttons.add(Box.createHorizontalStrut(5))
    buttons.add(cancel)
    ok.setPreferredSize(cancel.getPreferredSize)

    val content = new JPanel(new BorderLayout(0, 3))
    content.setBorder(new EmptyBorder(10, 10, 10, 10))
    content.add(field, BorderLayout.NORTH)
    content.add(buttons, BorderLayout.SOUTH)

    dialog.getRootPane.registerKeyboardAction(OkAction,
      KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW)

    dialog.getRootPane.registerKeyboardAction(CancelAction,
      KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW)

    dialog.getRootPane.setDefaultButton(ok)
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
    dialog.setContentPane(content)

    dialog.addWindowListener(new WindowAdapter() {
      override def windowClosed(e: WindowEvent) {
        if (!done) {
          done = true
          callback(None)
        }
      }
    })

    dialog
  }
}