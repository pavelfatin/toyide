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

import javax.swing.Timer

import com.pavelfatin.toyide.Language

import swing._
import scala.swing.event.{WindowOpened, WindowClosed}
import com.pavelfatin.toyide.document.Location
import java.awt.event.{ActionEvent, ActionListener, FocusAdapter, FocusEvent}
import com.pavelfatin.toyide.editor.{Pass, EditorFactory, HistoryImpl, Editor}

class MainFrame(language: Language, text: String) extends Frame {
  reactions += {
    case WindowOpened(_) =>
      timer.start()
    case WindowClosed(_) =>
      timer.stop()
      primaryEditor.dispose()
      secondaryEditor.dispose()
  }

  override def closeOperation() {
    launcher.stop()
    dispose()
  }

  private val history = new HistoryImpl()

  private val coloring = new DynamicColoring(language.colorings)

  private val primaryEditor = EditorFactory.createEditorFor(language, history, coloring)

  private lazy val secondaryEditor = EditorFactory.createEditorFor(primaryEditor.document,
    primaryEditor.data, primaryEditor.holder, language, history, coloring)

  private val data = primaryEditor.data

  private val timer = new Timer(10, new ActionListener() {
    def actionPerformed(e: ActionEvent) {
      if (data.hasNextPass) {
        data.nextPass()
      }
    }
  })

  timer.setRepeats(false)

  data.onChange { _ =>
    if (data.hasNextPass) {
      val delay = if (data.pass == Pass.Text) 300 else 100
      timer.setInitialDelay(delay)
      timer.restart()
    }
  }

  private val status = new StatusBar()

  private val tab = new EditorTabImpl(language.fileType, history, primaryEditor, secondaryEditor)

  private val console = new ConsoleImpl(coloring)

  private val launcher = new LauncherImpl()

  private val menu = new MainMenu(tab, this, primaryEditor.data, new NodeInterpreter(console),
    new NodeInvoker(console), launcher, console, coloring, language.examples)

  private def updateTitle() {
    val name = tab.file.map(_.getName.replaceAll("\\.%s".format(language.fileType.extension), ""))
    title = "%s - ToyIDE 1.2".format(name.getOrElse("Untitled"))
  }

  private def updateMessageFor(editor: Editor) {
    status.message = editor.message.mkString
  }

  private def updateCaretLocationFor(editor: Editor) {
    val Location(line, indent) = editor.document.toLocation(editor.terminal.offset)
    val selection = editor.terminal.selection.map(_.length.formatted("/%d")).mkString
    status.position = "%d:%d%s".format(line + 1, indent + 1, selection)
  }

  private def register(editor: Editor) {
    editor.onChange {
      if (editor.pane.hasFocus) updateMessageFor(editor)
    }
    editor.terminal.onChange { event =>
      if (editor.pane.hasFocus) updateCaretLocationFor(editor)
    }
    // TODO use scala.swing listener
    editor.pane.peer.addFocusListener(new FocusAdapter {
      override def focusGained(e: FocusEvent) {
        updateMessageFor(editor)
        updateCaretLocationFor(editor)
        menu.bindTo(editor.actions)
      }
    })
  }

  register(primaryEditor)
  register(secondaryEditor)

  updateMessageFor(primaryEditor)
  updateCaretLocationFor(primaryEditor)
  menu.bindTo(primaryEditor.actions)

  contents = new BorderPanel() {
    val split = new SplitPane(Orientation.Horizontal, tab, new ScrollPane(Component.wrap(console)))
    split.dividerLocation = 507
    split.resizeWeight = 1.0D
    split.border = null
    add(split, BorderPanel.Position.Center)
    add(status, BorderPanel.Position.South)
  }

  menuBar = menu

  tab.onChange {
    updateTitle()
  }

  updateTitle()

  tab.text = text.filter(_ != '\r').trim
}