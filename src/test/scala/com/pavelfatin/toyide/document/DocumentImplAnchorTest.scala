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

package com.pavelfatin.toyide.document

import org.junit.Test
import org.junit.Assert._

class DocumentImplAnchorTest {
  @Test
  def nothingLeftBias() {
    assertBehaviorIs("[", "[") { document =>
      Unit
    }
    assertBehaviorIs("foo[bar", "foo[bar") { document =>
      Unit
    }
  }

  @Test
  def nothingRightBias() {
    assertBehaviorIs("]", "]") { document =>
      Unit
    }
    assertBehaviorIs("foo]bar", "foo]bar") { document =>
      Unit
    }
  }

  @Test
  def insertLeftBias() {
    assertBehaviorIs("foo[bar", "foSOMEo[bar") { document =>
      document.insert(2, "SOME")
    }
    assertBehaviorIs("foo[bar", "foo[bSOMEar") { document =>
      document.insert(4, "SOME")
    }
    assertBehaviorIs("foo[bar", "foo[SOMEbar") { document =>
      document.insert(3, "SOME")
    }
  }

  @Test
  def insertRightBias() {
    assertBehaviorIs("foo]bar", "foSOMEo]bar") { document =>
      document.insert(2, "SOME")
    }
    assertBehaviorIs("foo]bar", "foo]bSOMEar") { document =>
      document.insert(4, "SOME")
    }
    assertBehaviorIs("foo]bar", "fooSOME]bar") { document =>
      document.insert(3, "SOME")
    }
  }

  @Test
  def removeLeftBias() {
    assertBehaviorIs("foo[bar", "o[bar") { document =>
      document.remove(0, 2)
    }
    assertBehaviorIs("foo[bar", "foo[b") { document =>
      document.remove(4, 6)
    }
    assertBehaviorIs("foo[bar", "[bar") { document =>
      document.remove(0, 3)
    }
    assertBehaviorIs("foo[bar", "foo[") { document =>
      document.remove(3, 6)
    }
    assertBehaviorIs("foo[bar", "[") { document =>
      document.remove(0, 6)
    }
    assertBehaviorIs("foo[bar", "fo[ar") { document =>
      document.remove(2, 4)
    }
    assertBehaviorIs("foo[bar", "[ar") { document =>
      document.remove(0, 4)
    }
    assertBehaviorIs("foo[bar", "fo[") { document =>
      document.remove(2, 6)
    }
    assertBehaviorIs("foo[bar", "foo[bar") { document =>
      document.remove(0, 0)
    }
    assertBehaviorIs("foo[bar", "foo[bar") { document =>
      document.remove(6, 6)
    }
    assertBehaviorIs("foo[bar", "foo[bar") { document =>
      document.remove(3, 3)
    }
  }

  @Test
  def removeRightBias() {
    assertBehaviorIs("foo]bar", "o]bar") { document =>
      document.remove(0, 2)
    }
    assertBehaviorIs("foo]bar", "foo]b") { document =>
      document.remove(4, 6)
    }
    assertBehaviorIs("foo]bar", "]bar") { document =>
      document.remove(0, 3)
    }
    assertBehaviorIs("foo]bar", "foo]") { document =>
      document.remove(3, 6)
    }
    assertBehaviorIs("foo]bar", "]") { document =>
      document.remove(0, 6)
    }
    assertBehaviorIs("foo]bar", "fo]ar") { document =>
      document.remove(2, 4)
    }
    assertBehaviorIs("foo]bar", "]ar") { document =>
      document.remove(0, 4)
    }
    assertBehaviorIs("foo]bar", "fo]") { document =>
      document.remove(2, 6)
    }
    assertBehaviorIs("foo]bar", "foo]bar") { document =>
      document.remove(0, 0)
    }
    assertBehaviorIs("foo]bar", "foo]bar") { document =>
      document.remove(6, 6)
    }
    assertBehaviorIs("foo]bar", "foo]bar") { document =>
      document.remove(3, 3)
    }
  }

  @Test
  def replaceLeftBias() {
    assertBehaviorIs("foo[bar", "SOMEo[bar") { document =>
      document.replace(0, 2, "SOME")
    }
    assertBehaviorIs("foo[bar", "foo[bSOME") { document =>
      document.replace(4, 6, "SOME")
    }
    assertBehaviorIs("foo[bar", "SOME[bar") { document =>
      document.replace(0, 3, "SOME")
    }
    assertBehaviorIs("foo[bar", "foo[SOME") { document =>
      document.replace(3, 6, "SOME")
    }
    assertBehaviorIs("foo[bar", "SOM[E") { document =>
      document.replace(0, 6, "SOME")
    }
    assertBehaviorIs("foo[bar", "foS[OMEar") { document =>
      document.replace(2, 4, "SOME")
    }
    assertBehaviorIs("foo[bar", "SOM[Ear") { document =>
      document.replace(0, 4, "SOME")
    }
    assertBehaviorIs("foo[bar", "foS[OME") { document =>
      document.replace(2, 6, "SOME")
    }
    assertBehaviorIs("foo[bar", "SOMEfoo[bar") { document =>
      document.replace(0, 0, "SOME")
    }
    assertBehaviorIs("foo[bar", "foo[barSOME") { document =>
      document.replace(6, 6, "SOME")
    }
    assertBehaviorIs("foo[bar", "foo[SOMEbar") { document =>
      document.replace(3, 3, "SOME")
    }
  }

  @Test
  def replaceRightBias() {
    assertBehaviorIs("foo]bar", "SOMEo]bar") { document =>
      document.replace(0, 2, "SOME")
    }
    assertBehaviorIs("foo]bar", "foo]bSOME") { document =>
      document.replace(4, 6, "SOME")
    }
    assertBehaviorIs("foo]bar", "SOME]bar") { document =>
      document.replace(0, 3, "SOME")
    }
    assertBehaviorIs("foo]bar", "foo]SOME") { document =>
      document.replace(3, 6, "SOME")
    }
    assertBehaviorIs("foo]bar", "SOM]E") { document =>
      document.replace(0, 6, "SOME")
    }
    assertBehaviorIs("foo]bar", "foS]OMEar") { document =>
      document.replace(2, 4, "SOME")
    }
    assertBehaviorIs("foo]bar", "SOM]Ear") { document =>
      document.replace(0, 4, "SOME")
    }
    assertBehaviorIs("foo]bar", "foS]OME") { document =>
      document.replace(2, 6, "SOME")
    }
    assertBehaviorIs("foo]bar", "SOMEfoo]bar") { document =>
      document.replace(0, 0, "SOME")
    }
    assertBehaviorIs("foo]bar", "foo]barSOME") { document =>
      document.replace(6, 6, "SOME")
    }
    assertBehaviorIs("foo]bar", "fooSOME]bar") { document =>
      document.replace(3, 3, "SOME")
    }
  }

  private def assertBehaviorIs(before: String, after: String)(action: Document => Unit) {
    val document = new DocumentImpl(before.diff(Seq('[', ']')))
    val (index, bias) = if (before.contains("[")) (before.indexOf('['), Bias.Left) else (before.indexOf(']'), Bias.Right)
    val anchor = document.createAnchorAt(index, bias)
    action(document)
    val char = if (bias == Bias.Left) '[' else ']'
    val actual = new StringBuilder(document.text).insert(anchor.offset, char).toString()
    assertEquals(after, actual)
  }
}