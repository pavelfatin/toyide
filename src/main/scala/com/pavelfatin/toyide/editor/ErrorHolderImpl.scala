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

import com.pavelfatin.toyide.Interval
import com.pavelfatin.toyide.document.{Bias, Document}

private class ErrorHolderImpl(document: Document, data: Data) extends ErrorHolder {
  private var passToAnchoredErrors = Map.empty[Pass, Seq[AnchoredError]]

  data.onChange {
    case DataEvent(pass, passErrors) =>
      val passAnchoredErrors = passToAnchoredErrors.getOrElse(pass, Seq.empty)

      val previousPassErrors = passAnchoredErrors.map(_.toError)

      if (passErrors != previousPassErrors) {
        passAnchoredErrors.foreach(_.dispose())
        passToAnchoredErrors = passToAnchoredErrors.updated(pass, passErrors.map(new AnchoredError(_)).toVector)
        notifyObservers(ErrorsChanged(previousPassErrors, passErrors))
      }
    case _ =>
  }

  def errors: Seq[Error] = passToAnchoredErrors.flatMap(_._2.map(_.toError)).toVector


  private class AnchoredError(error: Error) {
    private val beginAnchor = document.createAnchorAt(error.interval.begin, Bias.Right)

    private val endAnchor = document.createAnchorAt(error.interval.end, Bias.Left)

    def toError = {
      val interval = Interval(beginAnchor.offset, beginAnchor.offset.max(endAnchor.offset))
      Error(interval, error.message, error.decoration, error.fatal)
    }

    def dispose() {
      beginAnchor.dispose()
      endAnchor.dispose()
    }
  }
}