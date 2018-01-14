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