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

import com.pavelfatin.toyide.document.Location

private class Grid(val cellSize: Size, val insets: Insets) {
  def toPoint(location: Location): Point =
    Point(insets.left + cellSize.width * location.indent,
      insets.top + cellSize.height * (location.line + 1))

  def toLocation(point: Point): Location = {
    val line = math.round((point.y - insets.top).toDouble / cellSize.height.toDouble).toInt - 1
    val indent = math.round((point.x - insets.left).toDouble / cellSize.width.toDouble).toInt
    Location(0.max(line), 0.max(indent))
  }

  def toSize(lines: Int, maximumIndent: Int): Size = {
    val edge = toPoint(Location(lines, maximumIndent))
    Size(edge.x + cellSize.width + insets.right, edge.y + insets.bottom)
  }
}

private case class Size(width: Int, height: Int)

private case class Insets(top: Int, left: Int, bottom: Int, right: Int)

private object Insets {
  implicit def fromAwtInsets(insets: java.awt.Insets) = Insets(insets.top, insets.left, insets.bottom, insets.right)
}

private case class Point(x: Int, y: Int) {
  def +(size: Size) = Point(x + size.width, y + size.height)
}

private object Point {
  implicit def fromAwtPoint(point: java.awt.Point) = Point(point.x, point.y)
}