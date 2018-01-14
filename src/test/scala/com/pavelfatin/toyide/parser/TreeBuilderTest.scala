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

package com.pavelfatin.toyide.parser

import org.junit.Test
import org.junit.Assert._
import com.pavelfatin.toyide.node.{Node, NodeImpl}
import com.pavelfatin.toyide.{MockNode, Span, MockLexer}

class TreeBuilderTest {
  def builderFor(s: String) = {
    new TreeBuilder(MockLexer.analyze(s))
  }

  def assertBuild(tree: Node, expectation: String) {
    assertEquals(expectation.trim.replace("\r\n", "\n"), tree.content.trim)
  }

  //TODO error, zero-span node

  @Test
  def EOF() {
    val b1 = builderFor("")
    assertTrue(b1.isEOF)

    val b2 = builderFor("Foo")
    assertFalse(b2.isEOF)
    consumeFrom(b2)
    assertTrue(b2.isEOF)

    val b3 = builderFor("FooBar")
    assertFalse(b3.isEOF)
    consumeFrom(b3)
    assertFalse(b3.isEOF)
    consumeFrom(b3)
    assertTrue(b3.isEOF)

    val b4 = builderFor("FooBarMoo")
    assertFalse(b4.isEOF)
    consumeFrom(b4)
    assertFalse(b4.isEOF)
    consumeFrom(b4)
    assertFalse(b4.isEOF)
    consumeFrom(b4)
    assertTrue(b4.isEOF)
  }

  @Test(expected = classOf[NoRootNodeException])
  def noRootNode() {
    builderFor("").tree
  }

  @Test(expected = classOf[MultipleClosingException])
  def multipleClosing() {
    val b = builderFor("")
    val r = b.open()
    r.close(new MockNode())
    r.close(new MockNode())
  }

  @Test(expected = classOf[MultipleRootNodesException])
  def multipleRootNodes() {
    val b = builderFor("FooBar")

    val r1 = b.open()
    b.consume()
    r1.close(new MockNode())

    val r2 = b.open()
    b.consume()
    r2.close(new MockNode())

    b.tree
  }

  @Test(expected = classOf[NoSuchTokenException])
  def noSuchToken() {
    builderFor("").consume()
  }

  @Test(expected = classOf[ConsumeWithoutRegionException])
  def consumeWithoutRegion() {
    builderFor("Foo").consume()
  }

  @Test(expected = classOf[UnclosedRegionException])
  def unclosedRegion() {
    val b = builderFor("")
    b.open()
    b.tree
  }

  @Test(expected = classOf[IncorrectRegionsOrderException])
  def incorrectRegionsOrder() {
    val b = builderFor("")
    val r1 = b.open()
    b.open()
    r1.close(new MockNode())
  }

  @Test
  def unexpectedToken() {
    val s = "Foo"
    val b = builderFor(s)
    val m = b.open()
    b.error("message")
    m.close(new MockNode())
    val tree = b.tree

    assertBuild(tree, """
node
  error: Foo
""")

    assertEquals(Span(s, 0, 3), tree.span)
    assertEquals(Span(s, 0, 3), tree.children(0).span)

    assertEquals(None, tree.parent)
    assertEquals(Some(tree), tree.children(0).parent)

    assertEquals(None, tree.previousSibling)
    assertEquals(None, tree.nextSibling)

    assertEquals(None, tree.children(0).previousSibling)
    assertEquals(None, tree.children(0).nextSibling)
  }

@Test
  def unexpectedTokens() {
    val s = "Foo"
    val b = builderFor(s)
    val m = b.open()
    b.error("message1")
    b.error("message2")
    m.close(new MockNode())
    val tree = b.tree

    assertBuild(tree, """
node
  error: Foo
""")

    assertEquals("message1", tree.children(0).problem.mkString)
  }

  @Test
  def tokenExpected() {
    val s = "Foo"
    val b = builderFor(s)
    val m = b.open()
    b.consume()
    b.error("message")
    m.close(new MockNode())
    val tree = b.tree

    assertBuild(tree, """
node
  Foo
  error: leaf
""")

    assertEquals(Span(s, 0, 3), tree.span)
    assertEquals(Span(s, 0, 3), tree.children(0).span)
    assertEquals(Span(s, 3, 3), tree.children(1).span)

    assertEquals(None, tree.parent)
    assertEquals(Some(tree), tree.children(0).parent)
    assertEquals(Some(tree), tree.children(1).parent)

    assertEquals(None, tree.previousSibling)
    assertEquals(None, tree.nextSibling)

    assertEquals(None, tree.children(0).previousSibling)
    assertEquals(Some(tree.children(1)), tree.children(0).nextSibling)

    assertEquals(Some(tree.children(0)), tree.children(1).previousSibling)
    assertEquals(None, tree.children(1).nextSibling)
  }

  @Test
  def tokensExpected() {
    val s = "Foo"
    val b = builderFor(s)
    val m = b.open()
    b.consume()
    b.error("message1")
    b.error("message2")
    m.close(new MockNode())
    val tree = b.tree

    assertBuild(tree, """
node
  Foo
  error: leaf
""")

    assertEquals("message1", tree.children(1).problem.mkString)
  }

  @Test
  def single() {
    val s = "Foo"
    val b = builderFor(s)
    val m = b.open()
    b.consume()
    m.close(new MockNode())
    val tree = b.tree

    assertBuild(tree, """
node
  Foo
""")

    assertEquals(Span(s, 0, 3), tree.span)
    assertEquals(Span(s, 0, 3), tree.children(0).span)

    assertEquals(None, tree.parent)
    assertEquals(Some(tree), tree.children(0).parent)

    assertEquals(None, tree.previousSibling)
    assertEquals(None, tree.nextSibling)

    assertEquals(None, tree.children(0).previousSibling)
    assertEquals(None, tree.children(0).nextSibling)
  }

  @Test
  def subsequent() {
    val s = "FooBarMoo"
    val b = builderFor(s)
    val m = b.open()
    b.consume()
    b.consume()
    b.consume()
    m.close(new MockNode())
    val tree = b.tree

    assertBuild(tree, """
node
  Foo
  Bar
  Moo
""")

    assertEquals(Span(s, 0, 9), tree.span)
    assertEquals(Span(s, 0, 3), tree.children(0).span)
    assertEquals(Span(s, 3, 6), tree.children(1).span)
    assertEquals(Span(s, 6, 9), tree.children(2).span)

    assertEquals(None, tree.parent)
    assertEquals(Some(tree), tree.children(0).parent)
    assertEquals(Some(tree), tree.children(1).parent)
    assertEquals(Some(tree), tree.children(2).parent)

    assertEquals(None, tree.nextSibling)
    assertEquals(None, tree.previousSibling)

    assertEquals(None, tree.children(0).previousSibling)
    assertEquals(Some(tree.children(1)), tree.children(0).nextSibling)

    assertEquals(Some(tree.children(0)), tree.children(1).previousSibling)
    assertEquals(Some(tree.children(2)), tree.children(1).nextSibling)

    assertEquals(Some(tree.children(1)), tree.children(2).previousSibling)
    assertEquals(None, tree.children(2).nextSibling)
  }

  @Test
  def gap() {
    val s = "Foo Bar"
    val b = builderFor(s)
    val m = b.open()
    b.consume()
    b.consume()
    m.close(new MockNode())
    val tree = b.tree

    assertBuild(tree, """
node
  Foo
  Bar
  """)

    assertEquals(Span(s, 0, 7), tree.span)
    assertEquals(Span(s, 0, 3), tree.children(0).span)
    assertEquals(Span(s, 4, 7), tree.children(1).span)
  }

  @Test
  def hierarchy() {
    val s = "RedGreenBlueYellow"
    val b = builderFor(s)
    val r1 = b.open()
    b.consume() // Red
    val r2 = b.open()
    val r3 = b.open()
    b.consume() // Green
    r3.close(new MockNode())
    b.consume() // Blue
    r2.close(new MockNode())
    b.consume() // Yellow
    r1.close(new MockNode())
    val tree = b.tree

    assertBuild(tree, """
node
  Red
  node
    node
      Green
    Blue
  Yellow
""")

    val elements = tree.elements
    val spans = elements.map(_.span)
    assertEquals(Span(s, 0, 18), spans(0))
    assertEquals(Span(s, 0, 3), spans(1))
    assertEquals(Span(s, 3, 12), spans(2))
    assertEquals(Span(s, 3, 8), spans(3))
    assertEquals(Span(s, 3, 8), spans(4))
    assertEquals(Span(s, 8, 12), spans(5))
    assertEquals(Span(s, 12, 18), spans(6))

    val parents = elements.map(_.parent)
    assertEquals(None, parents(0))
    assertEquals(Some(tree), parents(1))
    assertEquals(Some(tree), parents(2))
    assertEquals(Some(elements(2)), parents(3))
    assertEquals(Some(elements(3)), parents(4))
    assertEquals(Some(elements(2)), parents(5))
    assertEquals(Some(elements(0)), parents(6))

    val previous = elements.map(_.previousSibling)
    assertEquals(None, previous(0))
    assertEquals(None, previous(1))
    assertEquals(Some(elements(1)), previous(2))
    assertEquals(None, previous(3))
    assertEquals(None, previous(4))
    assertEquals(Some(elements(3)), previous(5))
    assertEquals(Some(elements(2)), previous(6))

    val next = elements.map(_.nextSibling)
    assertEquals(None, next(0))
    assertEquals(Some(elements(2)), next(1))
    assertEquals(Some(elements(6)), next(2))
    assertEquals(Some(elements(5)), next(3))
    assertEquals(None, next(4))
    assertEquals(None, next(5))
    assertEquals(None, next(6))
  }

  @Test
  def collapseHolderNode() {
    val s = "FooBar"
    val b = builderFor(s)
    val r1 = b.open()
    val r2 = b.open()
    b.consume()
    b.consume()
    r2.close(new NodeImpl("nodeB"))
    r1.close(new NodeImpl("nodeA"), true)
    val tree = b.tree

    assertBuild(tree, """
nodeB
  Foo
  Bar
""")

    assertEquals(Span(s, 0, 6), tree.span)
    assertEquals(Span(s, 0, 3), tree.children(0).span)
    assertEquals(Span(s, 3, 6), tree.children(1).span)

    assertEquals(None, tree.parent)
    assertEquals(Some(tree), tree.children(0).parent)
    assertEquals(Some(tree), tree.children(1).parent)

    assertEquals(None, tree.nextSibling)
    assertEquals(None, tree.previousSibling)

    assertEquals(None, tree.children(0).previousSibling)
    assertEquals(Some(tree.children(1)), tree.children(0).nextSibling)

    assertEquals(Some(tree.children(0)), tree.children(1).previousSibling)
    assertEquals(None, tree.children(1).nextSibling)
  }

  @Test
  def collapseSeveralHolderNodes() {
    val s = "FooBar"
    val b = builderFor(s)
    val r1 = b.open()

    val r2 = b.open()
    b.consume()
    r2.close(new NodeImpl("nodeB"), true)

    val r3 = b.open()
    b.consume()
    r3.close(new NodeImpl("nodeB"), true)

    r1.close(new NodeImpl("nodeA"))
    assertBuild(b.tree, """
nodeA
  Foo
  Bar
""")
  }

   @Test
  def collapseSingleNode() {
    val s = "Foo"
    val b = builderFor(s)
    val r1 = b.open()
    b.consume()
    r1.close(new MockNode(), true)
    assertBuild(b.tree, """
Foo
""")
  }

  @Test
  def doNotCollapseHoldersWithMultipleNodes() {
    val s = "FooBar"
    val b = builderFor(s)
    val r1 = b.open()

    val r2 = b.open()
    b.consume()
    r2.close(new NodeImpl("nodeB"))

    val r3 = b.open()
    b.consume()
    r3.close(new NodeImpl("nodeB"))

    r1.close(new NodeImpl("nodeA"), true)
    assertBuild(b.tree, """
nodeA
  nodeB
    Foo
  nodeB
    Bar
""")
  }

  @Test
  def zeroSpanNode() {
    val s = ""
    val b = builderFor(s)
    val m = b.open()
    m.close(new MockNode())

    val tree = b.tree

    assertEquals(Span(s, 0, 0), tree.span)
    assertEquals(None, tree.parent)
    assertEquals(0, tree.children.size)
    assertEquals(None, tree.previousSibling)
    assertEquals(None, tree.nextSibling)
  }

  @Test
  def zeroSpanFirst() {
    val s = "Foo"
    val b = builderFor(s)
    val m = b.open()
    m.close(new MockNode())

    val tree = b.tree

    assertEquals(Span(s, 0, 0), tree.span)
    assertEquals(None, tree.parent)
    assertEquals(0, tree.children.size)
    assertEquals(None, tree.previousSibling)
    assertEquals(None, tree.nextSibling)
  }

  @Test
  def fold() {
    val s = "RedAndGreenAndBlueAndYellow"
    val b = builderFor(s)
    val r1 = b.open()
    while(!b.isEOF) b.consume()
    r1.fold(new MockNode())

    val tree = b.tree

    assertBuild(tree, """
node
  node
    node
      Red
      And
      Green
    And
    Blue
  And
  Yellow
""")
  }

  @Test
  def foldSingleLevel() {
    val s = "RedAndGreen"
    val b = builderFor(s)
    val r1 = b.open()
    b.consume()
    b.consume()
    b.consume()
    r1.fold(new MockNode())

    val tree = b.tree

    assertBuild(tree, """
node
  Red
  And
  Green
""")
  }

  @Test
  def foldIncompleteLevel() {
    val s = "RedAnd"
    val b = builderFor(s)
    val r1 = b.open()
    b.consume()
    b.consume()
    r1.fold(new MockNode())

    val tree = b.tree

    assertBuild(tree, """
node
  Red
  And
""")
  }

  @Test
  def foldSingleNode() {
    val s = "Red"
    val b = builderFor(s)
    val r1 = b.open()
    b.consume()
    r1.fold(new MockNode())

    val tree = b.tree

    assertBuild(tree, """
node
  Red
""")
  }

  @Test
  def foldEmpty() {
    val s = ""
    val b = builderFor(s)
    val r1 = b.open()
    r1.fold(new MockNode())

    val tree = b.tree

    assertBuild(tree, """
node
""")
  }

//  @Test
//  def zeroSpanLast {
//    val s = "Foo"
//    val b = builderFor(s)
//
//    val r1 = b.startRegion()
//    consumeFrom(b)
//
//    val r2 = b.startRegion()
//    r2.capture(MockNodeKind)
//
//    r1.capture(MockNodeKind)
//
//    val node = b.tree.children(1)
//
//    assertEquals(Span(s, 3, 3), node.span)
//    assertEquals(None, node.parent)
//    assertEquals(0, node.children.size)
//    assertEquals(None, node.previousSibling)
//    assertEquals(None, node.nextSibling)
//  }
//
//  @Test
//  def zeroSpanMiddle {
//    val s = "FooBar"
//    val b = builderFor(s)
//
//    val r1 = b.startRegion()
//    consumeFrom(b)
//
//    val r2 = b.startRegion()
//    r2.capture(MockNodeKind)
//
//    r1.capture(MockNodeKind)
//
//    val node = b.tree.children(1)
//
//    assertEquals(Span(s, 3, 3), node.span)
//    assertEquals(None, node.parent)
//    assertEquals(0, node.children.size)
//    assertEquals(None, node.previousSibling)
//    assertEquals(None, node.nextSibling)
//  }

  private def consumeFrom(in: TreeBuilder) {
    val region = in.open()
    in.consume()
    region.close(new MockNode())
  }
}


