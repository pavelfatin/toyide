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

package com.pavelfatin.toyide.document

import com.pavelfatin.toyide.Interval

class AnchoredInterval(document: Document, origin: Interval, beginBias: Bias = Bias.Right, endBias: Bias = Bias.Left) {
  private val beginAnchor = document.createAnchorAt(origin.begin, beginBias)

  private val endAnchor = document.createAnchorAt(origin.end, endBias)

  def interval = Interval(beginAnchor.offset, beginAnchor.offset.max(endAnchor.offset))

  def dispose() {
    beginAnchor.dispose()
    endAnchor.dispose()
  }
}
