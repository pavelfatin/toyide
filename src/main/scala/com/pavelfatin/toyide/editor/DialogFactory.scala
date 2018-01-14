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