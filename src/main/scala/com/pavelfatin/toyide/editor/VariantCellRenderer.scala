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
