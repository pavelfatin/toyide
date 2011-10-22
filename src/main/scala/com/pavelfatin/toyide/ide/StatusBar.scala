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

import swing.{Alignment, Label, FlowPanel}

private class StatusBar extends FlowPanel(FlowPanel.Alignment.Left)() {
  private val _message = new Label() {
    horizontalAlignment = Alignment.Leading
  }

  private val _position = new Label() {
    horizontalAlignment = Alignment.Leading
  }

  contents += _position

  contents += _message

  def message = _message.text

  def message_=(s: String) {
    _message.text = s
  }

  def position = _position.text

  def position_=(s: String) {
    _position.text = s
  }
}