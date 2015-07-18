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

import javax.swing.tree.TreeNode
import com.pavelfatin.toyide.node.Node
import collection.JavaConversions._

private case class TreeNodeAdapter(delegate: Node) extends TreeNode {
  private def convert(node: Node) = new TreeNodeAdapter(node)

  def children = delegate.children.map(convert).toIterator

  def isLeaf = delegate.isLeaf

  def getAllowsChildren = !isLeaf

  def getIndex(node: TreeNode) =
    delegate.children.indexWhere(node.asInstanceOf[TreeNodeAdapter].delegate == _)

  def getParent = delegate.parent.map(convert).orNull

  def getChildCount = delegate.children.size

  def getChildAt(childIndex: Int) = convert(delegate.children(childIndex))

  override def toString = delegate.toString
}