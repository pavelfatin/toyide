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

import java.awt.Dimension
import javax.swing.UIManager
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel
import com.pavelfatin.toyide.languages.toy.ToyExamples
import com.pavelfatin.toyide.ide.MainFrame
import swing.SimpleSwingApplication

object Application extends SimpleSwingApplication {
  private val InitialText = ToyExamples.Euler1.filterNot(_ == '\r').trim

  private lazy val frame = new MainFrame(InitialText)

  def top = frame

  override def startup(args: Array[String]) {
    UIManager.setLookAndFeel(new NimbusLookAndFeel())

    def frame = top

    frame.preferredSize = new Dimension(874, 696)
    frame.pack()
    frame.centerOnScreen()
    frame.open()
  }
}