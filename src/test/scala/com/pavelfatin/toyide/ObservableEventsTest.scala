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

import org.junit.Test
import org.junit.Assert._

class ObservableEventsTest {
  @Test
  def singleNotification() {
    val observable = new ObservableEvents[String]() {}
    var events = List[String]()
    observable.onChange(events ::= _)
    observable.notifyObservers("foo")
    assertEquals(List("foo"), events.reverse)
  }

  @Test
  def multipleNotifications() {
    val observable = new ObservableEvents[String]() {}
    var events = List[String]()
    observable.onChange(events ::= _)
    observable.notifyObservers("foo")
    observable.notifyObservers("bar")
    assertEquals(List("foo", "bar"), events.reverse)
  }

  @Test
  def disconnection() {
    val observable = new ObservableEvents[String]() {}
    var events = List[String]()
    val recorder = events ::= (_: String)
    observable.onChange(recorder)
    observable.notifyObservers("foo")
    observable.disconnect(recorder)
    observable.notifyObservers("bar")
    assertEquals(List("foo"), events.reverse)
  }
}