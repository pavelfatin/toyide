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

package com.pavelfatin.toyide.ide.action

import swing.Action
import com.pavelfatin.toyide.ide.EditorTab

class ExampleAction(title0: String, mnemonic0: Char, tab: EditorTab, code: String) extends Action(title0) {
  mnemonic = mnemonic0

  def apply() {
    tab.text = code.filterNot(_ == '\r').trim
  }
}