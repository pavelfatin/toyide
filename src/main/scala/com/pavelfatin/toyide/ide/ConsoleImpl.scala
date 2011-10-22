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

import javax.swing.text.{AttributeSet, StyleConstants, SimpleAttributeSet}
import javax.swing.{JTextPane, SwingUtilities}
import java.awt.{Color, Font}

private class ConsoleImpl extends JTextPane with Console {
  setFont(new Font("Monospaced", Font.PLAIN, 14))
  setEditable(false)

  def print(s: String) {
    doPrint(s, null)
  }

  def print(s: String, color: Color) {
    val attributes = new SimpleAttributeSet()
    StyleConstants.setForeground(attributes, color);
    doPrint(s, attributes)
  }

  def printLink(s: String, line: Int) {
    val attributes = new SimpleAttributeSet();
    StyleConstants.setForeground(attributes, new Color(125, 121, 111));
    doPrint(s, attributes)
  }

  private def doPrint(s: String, attributes: AttributeSet) {
    SwingUtilities.invokeLater(new Runnable {
      def run() {
        getDocument.insertString(getDocument.getLength, s, attributes)
      }
    })
  }

  def clear() {
    setText("")
  }
}