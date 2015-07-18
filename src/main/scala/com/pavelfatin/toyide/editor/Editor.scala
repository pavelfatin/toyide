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

import com.pavelfatin.toyide.Observable
import com.pavelfatin.toyide.document.Document
import swing.Component

trait Editor extends Observable {
  def document: Document

  def data: Data

  def holder: ErrorHolder

  def terminal: Terminal

  def component: Component

  def pane: Component

  def text: String

  def text_=(s: String)

  def actions: EditorActions

  def message: Option[String]

  def dispose()
}