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