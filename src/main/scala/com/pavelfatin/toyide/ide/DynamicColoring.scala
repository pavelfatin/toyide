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

import com.pavelfatin.toyide.Observable
import com.pavelfatin.toyide.editor.Coloring
import com.pavelfatin.toyide.lexer.TokenKind

private class DynamicColoring(delegates: Map[String, Coloring]) extends Coloring with Observable {
  private var _name: String = delegates.head._1
  
  private var _coloring: Coloring = delegates.head._2

  def names: Seq[String] = delegates.keys.toSeq
  
  def name: String = _name 
  
  def name_=(name: String) {
    if (_name != name) {
      _name = name
      _coloring = delegates(name)

      notifyObservers()
    }
  }

  def apply(id: String) = _coloring(id)

  def fontFamily = _coloring.fontFamily

  def fontSize: Int = _coloring.fontSize

  def attributesFor(kind: TokenKind) = _coloring.attributesFor(kind)
}
