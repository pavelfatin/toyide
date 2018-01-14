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

package com.pavelfatin.toyide.editor

import java.awt.Font
import javax.swing.border.EmptyBorder
import javax.swing.{DefaultListCellRenderer, JComponent, JList, ListCellRenderer}

import com.pavelfatin.toyide.lexer.Lexer

private class VariantCellRenderer(lexer: Lexer, coloring: Coloring) extends ListCellRenderer[AnyRef] {
  private val delegate = new DefaultListCellRenderer().asInstanceOf[ListCellRenderer[AnyRef]]

  override def getListCellRendererComponent(list: JList[_ <: AnyRef], value: AnyRef, index: Int,
                                            isSelected: Boolean, cellHasFocus: Boolean) = {
    val s = value.toString

    val result = delegate.getListCellRendererComponent(list, s, index, isSelected, cellHasFocus)

    result.asInstanceOf[JComponent].setBorder(new EmptyBorder(2, 2, 2, 4))

    val tokens = lexer.analyze(s)
    if (tokens.hasNext) {
      if (coloring.attributesFor(tokens.next().kind).weight == Weight.Bold) {
        val prototype = result.getFont
        result.setFont(new Font(prototype.getFamily, Font.BOLD, prototype.getSize))
      }
    }

    result
  }
}
