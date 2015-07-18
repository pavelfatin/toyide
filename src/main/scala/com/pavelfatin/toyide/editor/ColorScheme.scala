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

import com.pavelfatin.toyide.editor.Coloring._

object ColorScheme {
  val LightColors = Map(
    TextForeground -> Color.BLACK,
    TextBackground -> Color.WHITE,
    CurrentLineBackground -> new Color(255, 255, 215),
    CaretForeground -> Color.BLACK,
    SelectionForeground -> Color.WHITE,
    SelectionBackground -> new Color(82, 109, 165),
    HighlightBackground -> new Color(224, 240, 255),
    HoverForeground -> Color.BLUE,
    PairedBraceBackground -> new Color(153, 204, 255),
    UnbalancedBraceBackground -> new Color(255, 220, 220),
    RedForeground -> Color.RED,
    UnderlineForeground -> Color.RED,
    DimForeground -> Color.GRAY,
    FillBackground -> new Color(246, 235, 188),
    BooleanLiteral -> new Color(0, 0, 128),
    IntegerLiteral -> new Color(0, 0, 255),
    CharLiteral -> new Color(0, 128, 0),
    StringLiteral -> new Color(0, 128, 0),
    Keyword -> new Color(0, 0, 128),
    Comment -> new Color(128, 128, 128)
  )

  val DarkColors = Map(
    TextForeground -> new Color(245, 245, 245),
    TextBackground -> new Color(20, 31, 46),
    CurrentLineBackground -> new Color(27, 43, 64),
    CaretForeground -> Color.WHITE,
    SelectionForeground -> Color.WHITE,
    SelectionBackground -> new Color(55, 87, 128),
    HighlightBackground -> new Color(0, 128, 128),
    HoverForeground -> new Color(88, 157, 246),
    PairedBraceBackground -> new Color(60, 95, 140),
    UnbalancedBraceBackground -> new Color(140, 60, 102),
    RedForeground -> Color.RED,
    UnderlineForeground -> new Color(226, 49, 51),
    DimForeground -> new Color(181, 181, 181),
    FillBackground -> new Color(82, 80, 58),
    BooleanLiteral -> new Color(0, 153, 255),
    IntegerLiteral -> new Color(255, 128, 128),
    CharLiteral -> new Color(160, 255, 160),
    StringLiteral -> new Color(160, 255, 160),
    Keyword -> new Color(0, 153, 255),
    Comment -> new Color(80, 240, 80)
  )
}


