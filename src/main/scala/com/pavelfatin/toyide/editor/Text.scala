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

import java.awt.Color
import com.pavelfatin.toyide.document.Location
import java.text.AttributedString
import java.awt.font.TextAttribute

case class Text(location: Location, s: String, attributes: Attributes) {
  def decorated: AttributedString = attributes.decorate(s)
}

case class Attributes(color: Color, background: Option[Color], weight: Weight, style: Style, underlined: Boolean) {
  def decorate(s: String): AttributedString = {
    val result = new AttributedString(s)

    result.addAttribute(TextAttribute.FAMILY, "Monospaced")
    result.addAttribute(TextAttribute.SIZE, 14)

    if(underlined)
      result.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON)

    if(weight == Weight.Bold)
      result.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD)

    if(style == Style.Italic)
      result.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE)

    result
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
