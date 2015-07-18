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

import com.pavelfatin.toyide.Language
import com.pavelfatin.toyide.document.{Document, DocumentImpl}

object EditorFactory {
  def createEditorFor(language: Language, history: History, coloring: Coloring): Editor = {
    val document = new DocumentImpl()

    val data = new DataImpl(document, language.lexer, language.parser, language.inspections)

    val holder = new ErrorHolderImpl(document, data)

    createEditorFor(document, data, holder, language, history, coloring)
  }

  def createEditorFor(document: Document, data: Data, holder: ErrorHolder, language: Language,
                      history: History, coloring: Coloring): Editor = {

    val listRenderer = new VariantCellRenderer(language.lexer, coloring)

    val matcher = new BraceMatcherImpl(language.complements)

    new EditorImpl(document, data, holder, language.lexer, coloring, matcher, language.format,
      language.adviser, listRenderer, language.comment, history)
  }
}