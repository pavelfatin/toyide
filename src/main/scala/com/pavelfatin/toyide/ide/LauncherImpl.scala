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

package com.pavelfatin.toyide.ide

import javax.swing.SwingUtilities

private class LauncherImpl extends Launcher {
  private var thread: Option[Thread] = None

  def launch(action: => Unit) {
    thread = Some(new Thread(new MyRunnable(() => action)))
    thread.foreach(_.start())
    notifyObservers()
  }

  def stop() {
    thread.foreach(_.stop())
    thread = None
    notifyObservers()
  }

  def active: Boolean = thread.isDefined

  private class MyRunnable(action: () => Unit) extends Runnable {
    def run() {
      action()

      SwingUtilities.invokeLater(new Runnable {
        def run() {
          stop()
        }
      })
    }
  }
}