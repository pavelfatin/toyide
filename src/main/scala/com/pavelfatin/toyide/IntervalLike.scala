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

trait IntervalLike {
  def begin: Int

  def end: Int

  def length: Int = end - begin

  def empty: Boolean = length == 0

  def includes(offset: Int): Boolean = begin <= offset && offset < end

  def touches(offset: Int): Boolean = begin <= offset && offset <= end

  def includes(interval: IntervalLike): Boolean =
    (!empty && !interval.empty) &&
      interval.begin >= begin && interval.end <=end

  def intersectsWith(interval: IntervalLike): Boolean =
    (!empty && !interval.empty) &&
      (includes(interval.begin) || includes(interval.end - 1) ||
        interval.includes(begin) || interval.includes(end - 1))
}
