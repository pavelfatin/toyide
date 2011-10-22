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