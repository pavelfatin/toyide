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

package com.pavelfatin.toyide.ide

import java.awt.{Color, Font}
import javax.swing.text.{AttributeSet, SimpleAttributeSet, StyleConstants}
import javax.swing.{JTextPane, SwingUtilities}

import com.pavelfatin.toyide.editor.Coloring

private class ConsoleImpl(coloring: Coloring) extends JTextPane with Console {
  private val LinkColor = new Color(125, 121, 111)

  setFont(new Font(coloring.fontFamily, Font.PLAIN, coloring.fontSize))
  setEditable(false)

  def print(s: String) {
    doPrint(s, null)
  }

  def print(s: String, color: Color) {
    val attributes = new SimpleAttributeSet()
    StyleConstants.setForeground(attributes, color)
    doPrint(s, attributes)
  }

  def printLink(s: String, line: Int) {
    val attributes = new SimpleAttributeSet()
    StyleConstants.setForeground(attributes, LinkColor)
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