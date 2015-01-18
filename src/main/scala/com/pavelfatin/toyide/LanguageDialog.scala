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
