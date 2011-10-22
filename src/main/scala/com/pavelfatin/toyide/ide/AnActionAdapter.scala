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

import swing.Action
import javax.swing.{SwingUtilities, KeyStroke}
import com.pavelfatin.toyide.editor.AnAction

private class AnActionAdapter(title: String, key: Char, anAction: AnAction) extends Action(title) {
  mnemonic = key

  accelerator = Some(KeyStroke.getKeyStroke(anAction.keys.head))

  enabled = anAction.enabled

  anAction.onChange {
    SwingUtilities.invokeLater(new Runnable {
      def run() {
        enabled = anAction.enabled
      }
    })
  }

  def apply() {
    anAction()
  }
}