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

package com.pavelfatin.toyide.editor.painter

import com.pavelfatin.toyide.ObservableEvents
import com.pavelfatin.toyide.editor.{ActionFinished, ActionProcessor, ActionStarted}

private class Delay[T](delegate: ObservableEvents[T], processor: ActionProcessor) extends ObservableEvents[T] {
  private var delay = false
  
  private var events = Seq.empty[T]

  processor.onChange {
    case ActionStarted => 
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
