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