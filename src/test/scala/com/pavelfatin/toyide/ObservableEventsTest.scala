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