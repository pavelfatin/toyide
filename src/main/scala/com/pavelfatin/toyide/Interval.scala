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

package com.pavelfatin.toyide

case class Interval(begin: Int, end: Int) extends IntervalLike {
  if(begin < 0) throw new IllegalArgumentException("Begin must be positive: " + begin)
  if(end < 0) throw new IllegalArgumentException("End must be positive: " + end)
  if(length < 0) throw new IllegalArgumentException("Length must be positive: " + length)

  def intersection(interval: Interval): Interval = {
    val from = begin.max(interval.begin)
    Interval(from, from.max(end.min(interval.end)))
  }

  def withBeginShift(n: Int) = copy(begin = begin + n)

  def withEndShift(n: Int) = copy(end = end + n)

  def +(n: Int) = Interval(begin + n, end + n)

  def -(n: Int) = Interval(begin - n, end - n)

  def transformWith(f: Int => Int) = copy(begin = f(begin), end = f(end))
}