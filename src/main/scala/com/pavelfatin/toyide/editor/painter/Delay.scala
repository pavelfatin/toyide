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

package com.pavelfatin.toyide.editor.painter

import com.pavelfatin.toyide.ObservableEvents
import com.pavelfatin.toyide.editor.{ActionFinished, ActionProcessor, ActionStarted}

private class Delay[T](delegate: ObservableEvents[T], processor: ActionProcessor) extends ObservableEvents[T] {
  private var delay = false
  
  private var events = Seq.empty[T]

  processor.onChange {
    case ActionStarted(_) =>
      delay = true
    case ActionFinished =>
      events.foreach(notifyObservers)
      events = Seq.empty
      
      delay = false
  }
  
  delegate.onChange { event =>
    if (delay) {
      events = events :+ event
    } else {
      notifyObservers(event)
    }
  }
}
