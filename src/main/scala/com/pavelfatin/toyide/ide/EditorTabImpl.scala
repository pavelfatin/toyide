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