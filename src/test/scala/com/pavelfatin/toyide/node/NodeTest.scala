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

package com.pavelfatin.toyide.node

import org.junit.Test
import org.junit.Assert._
import com.pavelfatin.toyide.MockNode

class NodeTest {
  def node(children: NodeImpl*): NodeImpl = {
    val node = new MockNode()
    if(!children.isEmpty) node.children = children
    node
  }

  @Test
  def elements() {
    val a = node()
    val b = node()
    val c = node(a, b)
    val d = node()
    val e = node()
    val f = node(e)
    val g = node(d, c, f)
    assertEquals(Seq(g, d, c, a, b, f, e), g.elements)
  }

  @Test
  def nextSiblings() {
    val n1 = node()
    val n2 = node()
    val n3 = node()

    n1.nextSibling = Some(n2)
    n2.nextSibling = Some(n3)

    assertEquals(Seq(n2, n3), n1.nextSiblings)
    assertEquals(Seq(n3), n2.nextSiblings)
    assertEquals(Seq(), n3.nextSiblings)
  }

  @Test
  def previousSiblings() {
    val n1 = node()
    val n2 = node()
    val n3 = node()

    n3.previousSibling = Some(n2)
    n2.previousSibling = Some(n1)

    assertEquals(Seq(n2, n1), n3.previousSiblings)
    assertEquals(Seq(n1), n2.previousSiblings)
    assertEquals(Seq(), n1.previousSiblings)
  }
}