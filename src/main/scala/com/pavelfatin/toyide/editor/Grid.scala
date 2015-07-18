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

import java.awt.{Dimension, Insets, Point, Rectangle}

import com.pavelfatin.toyide.document.Location

class Grid(val cellSize: Dimension, val insets: Insets) {
  def toPoint(location: Location): Point =
    new Point(insets.left + cellSize.width * location.indent,
      insets.top + cellSize.height * location.line)

  def toLocation(point: Point): Location = {
    val line = math.floor((point.y - insets.top).toDouble / cellSize.height.toDouble).toInt
    val indent = math.floor((point.x - insets.left).toDouble / cellSize.width.toDouble).toInt
    Location(0.max(line), 0.max(indent))
  }

  def toSize(lines: Int, maximumIndent: Int): Dimension = {
    val edge = toPoint(Location(lines, maximumIndent))
    new Dimension(edge.x + cellSize.width + insets.right, edge.y + insets.bottom)
  }

  def toArea(rectangle: Rectangle): Area = {
    val beginLine = 0.max(math.floor((rectangle.y - insets.top).toDouble / cellSize.height.toDouble).toInt)
    val beginIndent = 0.max(math.floor((rectangle.x - insets.left).toDouble / cellSize.width.toDouble).toInt)

    val endLine = math.ceil((rectangle.y - insets.top + rectangle.height).toDouble / cellSize.height.toDouble).toInt
    val endIndent = math.ceil((rectangle.x - insets.left + rectangle.width).toDouble / cellSize.width.toDouble).toInt

    Area(beginLine, beginIndent, endIndent - beginIndent, endLine - beginLine)
  }

  def toRectangle(area: Area): Rectangle = {
    val point = toPoint(Location(area.line, area.indent))
    new Rectangle(point.x, point.y, cellSize.width * area.width, cellSize.height * area.height)
  }
}
