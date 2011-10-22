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

case class Interval(begin: Int, end: Int) {
  if(begin < 0) throw new IllegalArgumentException("Begin must be positive: " + begin)
  if(end < 0) throw new IllegalArgumentException("End must be positive: " + end)
  if(length < 0) throw new IllegalArgumentException("Length must be positive: " + length)

  def length = end - begin

  def empty = length == 0

  def includes(offset: Int) = begin <= offset && offset < end

  def touches(offset: Int) = begin <= offset && offset <= end

  def includes(interval: Interval) =
    (!empty && !interval.empty) &&
      interval.begin >= begin && interval.end <=end

  def intersectsWith(interval: Interval) =
    (!empty && !interval.empty) &&
    (includes(interval.begin) || includes(interval.end - 1) ||
    interval.includes(begin) || interval.includes(end - 1))

  def withBeginShift(n: Int) = copy(end = end + n)

  def withEndShift(n: Int) = copy(end = end + n)

  def transformWith(f: Int => Int) = copy(begin = f(begin), end = f(end))
}