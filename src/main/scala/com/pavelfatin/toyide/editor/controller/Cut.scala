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

package com.pavelfatin.toyide.editor.controller

import java.awt.datatransfer.StringSelection
import java.awt.Toolkit
import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.editor.{AnAction, Terminal}

private class Cut(document: Document, terminal: Terminal) extends AnAction {
  def keys = List("ctrl pressed X", "shift pressed DELETE")

  def apply() {
    if(terminal.selection.isEmpty)
      terminal.selection = Some(terminal.currentLineIntervalIn(document))

    terminal.selection.foreach { s =>
      val text = new StringSelection(document.text(s))
      Toolkit.getDefaultToolkit.getSystemClipboard.setContents(text, null)
      terminal.insertInto(document, "")
    }
  }
}