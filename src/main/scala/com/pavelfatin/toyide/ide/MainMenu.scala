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

import action._
import swing._
import event.Key
import javax.swing.KeyStroke
import com.pavelfatin.toyide.Example
import com.pavelfatin.toyide.editor._

private class MainMenu(tab: EditorTab, frame: Frame, data: Data, interpreter: Runner, invoker: Runner, launcher: Launcher,
                       console: Console, coloring: DynamicColoring, examples: Seq[Example]) extends MenuBar {

  private val undo = new MenuItem("")

  private val redo = new MenuItem("")

  private val complete = new MenuItem("")

  private val copy = new MenuItem("")

  private val cut = new MenuItem("")

  private val duplicateLine = new MenuItem("")

  private val indentSelection = new MenuItem("")

  private val unindentSelection = new MenuItem("")

  private val escape = new MenuItem("")

  private val format = new MenuItem("")

  private val gotoDeclaration = new MenuItem("")

  private val moveLineDown = new MenuItem("")

  private val moveLineUp = new MenuItem("")

  private val optimize = new MenuItem("")

  private val paste = new MenuItem("")

  private val removeLine = new MenuItem("")

  private val rename = new MenuItem("")

  private val selectAll = new MenuItem("")

  private val showUsages = new MenuItem("")

  private val toggleLineComment = new MenuItem("")

  def bindTo(actions: EditorActions) {
    bind(complete, "Complete", 'P', actions.complete)
    bind(copy, "Copy", 'C', actions.copy)
    bind(cut, "Cut", 'T', actions.cut)
    bind(duplicateLine, "Duplicate Line", 'D', actions.duplicateLine)
    bind(indentSelection, "Indent Selection", 'I', actions.indentSelection)
    bind(unindentSelection, "Unindent Selection", 'N', actions.unindentSelection)
    bind(escape, "Clear Selection", 'L', actions.escape)
    bind(format, "Reformat", 'F', actions.format)
    bind(gotoDeclaration, "Goto Declaration", 'G', actions.gotoDeclaration)
    bind(moveLineDown, "Move Line Down", 'D', actions.moveLineDown)
    bind(moveLineUp, "Move Line Up", 'U', actions.moveLineUp)
    bind(optimize, "Optimize", 'O', actions.optimize)
    bind(paste, "Paste", 'P', actions.paste)
    bind(removeLine, "Remove Line", 'R', actions.removeLine)
    bind(rename, "Rename", 'R', actions.rename)
    bind(selectAll, "Select All", 'A', actions.selectAll)
    bind(showUsages, "Show Usages", 'S', actions.showUsages)
    bind(toggleLineComment, "Toggle Line Comment", 'T', actions.toggleLineComment)
    bind(undo, "Undo", 'U', actions.undo)
    bind(redo, "Redo", 'R', actions.redo)
  }

  private def bind(item: MenuItem, title: String, mnemonic: Char, anAction: AnAction) {
    item.action = new AnActionAdapter(title, mnemonic, anAction)
  }

  contents += new Menu("File") {
    val parent = Component.wrap(frame.peer.getRootPane)
    mnemonic = Key.F
    contents += new MenuItem(new NewAction("New", 'N', "ctrl pressed N", parent, tab))
    contents += new MenuItem(new OpenAction("Open...", 'O', "ctrl pressed O", parent, tab))
    contents += new MenuItem(new SaveAction("Save", 'S', "ctrl pressed S", parent, tab))
    contents += new MenuItem(new SaveAsAction("Save As...", 'A', "shift ctrl pressed S", parent, tab))
    contents += new Separator()
    contents += new MenuItem(new ExportToClassAction("Export to Class...", 'E', data, parent))
    contents += new Separator()
    contents += new MenuItem(new Action("Exit") {
      mnemonic = 'X'
      def apply() {
        frame.dispose()
      }
    })
  }

  contents += new Menu("Edit") {
    mnemonic = Key.E

    contents += undo
    contents += redo
    contents += new Separator()
    contents += cut
    contents += copy
    contents += paste
    contents += new Separator()
    contents += selectAll
    contents += escape
    contents += new Separator()
    contents += duplicateLine
    contents += removeLine
    contents += indentSelection
    contents += unindentSelection
  }

  contents += new Menu("Code") {
    mnemonic = Key.D

    contents += gotoDeclaration
    contents += showUsages
    contents += new Separator()
    contents += complete
    contents += rename
    contents += new Separator()
    contents += toggleLineComment
    contents += new Separator()
    contents += optimize
    contents += format
    contents += new Separator()
    contents += moveLineUp
    contents += moveLineDown
  }

  contents += new Menu("Run") {
    mnemonic = Key.R

    contents += new MenuItem(new InterpretAction("Interpret", 'I', "shift pressed F10", data, interpreter, launcher, console))
    contents += new MenuItem(new InvokeAction("Compile", 'C', "shift ctrl pressed F10", data, invoker, launcher, console))
    contents += new Separator()
    contents += new MenuItem(new StopAction("Stop", 'S', "ctrl pressed F2", launcher, console))
  }

  contents += new Menu("Examples") {
    mnemonic = Key.X
    contents ++= examples.map(it => new MenuItem(new ExampleAction(it.name, it.mnemonic, tab, it.code)))
  }

  contents += new Menu("Coloring") {
    mnemonic = Key.C
    contents ++= coloring.names.map(it => new RadioMenuItem(it) {
      action = new ColoringAction(coloring, it)

      coloring.onChange {
        updateSelection()
      }

      updateSelection()

      private def updateSelection() {
        selected = coloring.name == it
      }
    })
  }

  contents += new Menu("Window") {
    mnemonic = Key.W

    contents += new CheckMenuItem("Split") {
      action = new Action(text) {
        mnemonic = 'S'
        accelerator = Some(KeyStroke.getKeyStroke("ctrl alt pressed S"))
        def apply() {
          tab.split = selected
        }
      }
    }
  }

  contents += new Menu("Help") {
    mnemonic = Key.H

    contents += new MenuItem(new Action("License") {
      mnemonic = 'L'

      def apply() {
        val dialog = new InfoDialog(frame, "license.html", true)
        dialog.title = "License"
        dialog.preferredSize = new Dimension(600, 500)
        dialog.pack()
        dialog.setLocationRelativeTo(frame)
        dialog.open()
      }
    })

    contents += new MenuItem(new Action("About") {
      mnemonic = 'A'
      accelerator = Some(KeyStroke.getKeyStroke("pressed F1"))

      def apply() {
        val dialog = new InfoDialog(frame, "about.html", false)
        dialog.title = "About the program"
        dialog.pack()
        dialog.setLocationRelativeTo(frame)
        dialog.open()
      }
    })
  }
}