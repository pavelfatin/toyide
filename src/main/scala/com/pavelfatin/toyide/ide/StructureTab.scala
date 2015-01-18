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

import javax.swing.JTree
import javax.swing.tree.DefaultTreeModel
import javax.swing.event.{TreeSelectionEvent, TreeSelectionListener}
import java.awt.event.{FocusEvent, FocusAdapter}
import com.pavelfatin.toyide.editor.{Terminal, Data}
import swing.{BorderPanel, Component}

private class StructureTab(data: Data, terminal: Terminal) extends BorderPanel {
  private val tree = new JTree()

  tree.setEditable(false)

  add(Component.wrap(tree), BorderPanel.Position.Center)

  data.onChange {
    data.structure match {
      case Some(node) => tree.setModel(new DefaultTreeModel(new TreeNodeAdapter(node)))
      case None => // do nothing
    }
  }

  tree.addTreeSelectionListener(new TreeSelectionListener() {
    def valueChanged(e: TreeSelectionEvent) {
      updateTreeHighlight()
    }
  })

  tree.addFocusListener(new FocusAdapter() {
    override def focusGained(e: FocusEvent) {
      terminal.selection = None
      updateTreeHighlight()
    }

    override def focusLost(e: FocusEvent) {
      terminal.highlights = Seq.empty
      tree.clearSelection()
    }
  })

  private def updateTreeHighlight() {
    val selection = Option(tree.getSelectionPath).map(_.getLastPathComponent.asInstanceOf[TreeNodeAdapter])
    selection.map(_.delegate).foreach { node =>
      terminal.highlights = Seq(node.span)
    }
  }
}