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