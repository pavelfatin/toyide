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

package com.pavelfatin.toyide.ide

import com.pavelfatin.toyide.FileType
import java.io.File
import swing._
import com.pavelfatin.toyide.editor.{History, Editor}

private class EditorTabImpl(val fileType: FileType, val history: History,
                            primaryEditor: Editor, secondaryEditor: => Editor) extends BorderPanel with EditorTab {
  private val structure = new StructureTab(primaryEditor.data, primaryEditor.terminal)

  private var _split = false

  private var _original = ""

  private var _file: Option[File] = None

  updateLayout()

  def text = primaryEditor.text

  def text_=(s: String) {
    primaryEditor.text = s
    _original = s
    history.clear()
  }

  def file = _file

  def file_=(file: Option[File]) {
    _file = file
    notifyObservers()
  }

  def changed = text != _original

  def split = _split

  def split_=(b: Boolean) {
    _split = b
    updateLayout()
    val editor = if (split) secondaryEditor else primaryEditor
    editor.pane.requestFocusInWindow()
  }

  private def updateLayout() {
    val editors = if (split) {
      val pane = new SplitPane(Orientation.Horizontal, primaryEditor.component, secondaryEditor.component)
      pane.resizeWeight = 0.5D
      pane.border = null
      pane
    } else {
      primaryEditor.component
    }

    val pane = new SplitPane(Orientation.Vertical, editors, new ScrollPane(structure))
    pane.resizeWeight = 0.7D

    peer.removeAll()
    add(pane, BorderPanel.Position.Center)
    revalidate()
  }
}