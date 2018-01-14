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

import java.awt.Color
import java.text.AttributedString
import java.awt.font.TextAttribute

case class Attributes(color: Color, background: Option[Color], weight: Weight, style: Style, underlined: Boolean) {
  def decorate(result: AttributedString, begin: Int, end: Int) {
    result.addAttribute(TextAttribute.FOREGROUND, color, begin, end)
    background.foreach(it => result.addAttribute(TextAttribute.BACKGROUND, it, begin, end))

    if(underlined)
      result.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, begin, end)

    if(weight == Weight.Bold)
      result.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, begin, end)

    if(style == Style.Italic)
      result.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, begin, end)
  }
}

abstract sealed class Weight

object Weight {
  case object Normal extends Weight

  case object Bold extends Weight
}

abstract sealed class Style

object Style {
  case object Ordinary extends Style

  case object Italic extends Style
}
