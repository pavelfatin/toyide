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