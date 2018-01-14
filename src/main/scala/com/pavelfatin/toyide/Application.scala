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

import java.awt.Dimension
import javax.swing.UIManager

import com.pavelfatin.toyide.ide.MainFrame
import com.pavelfatin.toyide.languages.lisp.LispLanguage
import com.pavelfatin.toyide.languages.toy.ToyLanguage
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel

import scala.swing._

object Application extends SwingApplication {
  private val LookAndFeel = new NimbusLookAndFeel()

  private val Languages = Seq(ToyLanguage, LispLanguage)

  override def startup(args: Array[String]) {
    UIManager.setLookAndFeel(LookAndFeel)

    // Workaround for https://bugs.openjdk.java.net/browse/JDK-8134828
    UIManager.put("ScrollBar.minimumThumbSize", new Dimension(32, 32))

    selectLanguage().foreach(openMainFrame(_))
  }

  private def selectLanguage(): Option[Language] = {
    val dialog = new LanguageDialog(Languages)
    open(dialog)
    dialog.selection
  }

  private def openMainFrame(language: Language) {
    val code = language.examples.headOption.fold("")(_.code)
    val frame = new MainFrame(language, code)
    frame.preferredSize = new Dimension(874, 696)
    open(frame)
  }

  private def open(window: Window) {
    window.pack()
    window.centerOnScreen()
    window.open()
  }
}