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

package com.pavelfatin.toyide.editor

import com.pavelfatin.toyide.document.{AnchoredInterval, Document}

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


  private class AnchoredError(error: Error) extends AnchoredInterval(document, error.interval) {
    def toError = Error(interval, error.message, error.decoration, error.fatal)
  }
}