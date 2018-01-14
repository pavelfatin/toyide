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

import javax.swing.JTree
import javax.swing.tree.DefaultTreeModel
import javax.swing.event.{TreeSelectionEvent, TreeSelectionListener}
import java.awt.event.{FocusEvent, FocusAdapter}
import com.pavelfatin.toyide.editor.{Pass, DataEvent, Terminal, Data}
import swing.{BorderPanel, Component}

private class StructureTab(data: Data, terminal: Terminal) extends BorderPanel {
  private val tree = new JTree()

  tree.setEditable(false)

  add(Component.wrap(tree), BorderPanel.Position.Center)

  data.onChange {
    case DataEvent(Pass.Parser, _) =>
      val root = data.structure.getOrElse(
        throw new IllegalStateException("No root node after parser pass"))

      tree.setModel(new DefaultTreeModel(new TreeNodeAdapter(root)))
    case _ =>
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
      terminal.highlights = Seq(node.span.interval)
    }
  }
}