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

import java.awt.Toolkit
import com.pavelfatin.toyide.document.Document
import java.awt.datatransfer.DataFlavor
import com.pavelfatin.toyide.editor.{AnAction, Terminal}

private class Paste(document: Document, terminal: Terminal) extends AnAction {
  def keys = List("ctrl pressed V", "shift pressed INSERT")

  def apply() {
    val contents = Toolkit.getDefaultToolkit.getSystemClipboard.getContents(null)
    if(contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      val text = contents.getTransferData(DataFlavor.stringFlavor).asInstanceOf[String]
      terminal.insertInto(document, text)
    }
  }
}