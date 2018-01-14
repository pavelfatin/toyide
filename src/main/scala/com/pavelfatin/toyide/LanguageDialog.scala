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

package com.pavelfatin.toyide

import java.awt.event.{MouseEvent, MouseAdapter, KeyEvent}
import javax.swing.table.AbstractTableModel
import javax.swing.{JComponent, JTable, KeyStroke, ListSelectionModel}

import scala.swing._
import scala.swing.event.WindowClosing

class LanguageDialog(languages: Seq[Language]) extends Dialog {
  private val table = new JTable(LangugeTableModel)
  private val okButton = new Button(Action("OK")(onOk()))
  private val cancelButton = new Button(Action("Cancel")(onCancel()))

  private var itemSelected = false

  modal = true
  title = "Language selection - ToyIDE"
  defaultButton = okButton
  preferredSize = new Dimension(350, 250)

  table.getColumnModel.getColumn(0).setMaxWidth(100)
  table.getSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
  table.getSelectionModel.setSelectionInterval(0, 0)

  table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none")

  table.addMouseListener(new MouseAdapter {
    override def mouseClicked(e: MouseEvent): Unit = {
      if (e.getClickCount == 2) onOk()
    }
  })

  okButton.preferredSize = cancelButton.preferredSize

  peer.getRootPane.registerKeyboardAction(cancelButton.action.peer,
    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW)

  reactions += {
    case WindowClosing(_) => onCancel()
  }

  contents = new BorderPanel() {
    border = Swing.EmptyBorder(10)
    layoutManager.setVgap(3)

    val contentPane = new ScrollPane(Component.wrap(table))

    val buttonsPane = new FlowPanel(FlowPanel.Alignment.Trailing)(
      okButton, Swing.HStrut(6), cancelButton) { hGap = 0; vGap = 0 }

    add(contentPane, BorderPanel.Position.Center)
    add(buttonsPane, BorderPanel.Position.South)
  }

  def selection: Option[Language] =
    if (itemSelected) Some(languages(table.getSelectedRow)) else None

  private def onOk() {
    itemSelected = true
    dispose()
  }

  private def onCancel() {
    dispose()
  }

  private object LangugeTableModel extends AbstractTableModel {
    def getRowCount = languages.length

    def getColumnCount = 2

    override def getColumnName(column: Int) = column match {
      case 0 => "Name"
      case 1 => "Description"
    }

    def getValueAt(rowIndex: Int, columnIndex: Int) = {
      val language = languages(rowIndex)

      columnIndex match {
        case 0 => language.name
        case 1 => language.description
      }
    }
  }
}
