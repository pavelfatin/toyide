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

import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.formatter.Formatter
import com.pavelfatin.toyide.editor._

private class Actions(document: Document, terminal: Terminal, data: Data, adviser: Adviser,
                      formatter: Formatter, tabSize: Int, comment: String, history: History) extends EditorActions {
  private def historical(action: AnAction) = new HistoricalAction(action, document, terminal, history)
    
  val complete = new Complete(document, terminal, data, adviser, history)

  val copy = historical(new Copy(document, terminal))

  val cut = historical(new Cut(document, terminal))

  val duplicateLine = historical(new DuplicateLine(document, terminal))

  val escape = historical(new Escape(terminal))

  val format = historical(new Format(document, terminal, data, formatter, tabSize))

  val gotoDeclaration = historical(new GotoDeclaration(terminal, data))

  val indentSelection = historical(new IndentSelection(document, terminal, tabSize))

  val moveLineDown = historical(new MoveLineDown(document, terminal))

  val moveLineUp = historical(new MoveLineUp(document, terminal))

  val optimize = historical(new Optimize(document, terminal, data))

  val paste = historical(new Paste(document, terminal))

  val redo = new Redo(document, terminal, history)

  val removeLine = historical(new RemoveLine(document, terminal))

  val rename = new Rename(document, terminal, data, history)

  val selectAll = historical(new SelectAll(document, terminal))

  val showUsages = historical(new ShowUsages(terminal, data))

  val toggleLineComment = historical(new ToggleLineComment(document, terminal, comment))

  val undo = new Undo(document, terminal, history)

  val unindentSelection = historical(new UnindentSelection(document, terminal, tabSize))
}