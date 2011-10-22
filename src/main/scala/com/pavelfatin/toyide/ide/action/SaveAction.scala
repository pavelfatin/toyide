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

import swing.{Component, Action}
import javax.swing.KeyStroke
import com.pavelfatin.toyide.ide.EditorTab

class SaveAction(title0: String, mnemonic0: Char, shortcut: String,
                         parent: Component, tab: EditorTab) extends Action(title0) {
  mnemonic = mnemonic0

  accelerator = Some(KeyStroke.getKeyStroke(shortcut))

  def apply() {
    if (tab.file.isDefined) {
      tab.file.foreach(IO.write(_, tab.text))
    } else {
      SaveAsAction.performOn(tab, parent)
    }
  }
}