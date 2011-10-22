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

package com.pavelfatin.toyide.editor.controller

import org.junit.Test

class DuplicateLineTest extends ActionTestBase(new DuplicateLine(_, _)) {
  @Test
  def line() {
    assertEffectIs("|", "\n|")
    assertEffectIs("f|oo", "foo\nf|oo")
    assertEffectIs("foo|", "foo\nfoo|")
    assertEffectIs("fo|o\nbar", "foo\nfo|o\nbar")
  }

  @Test
  def selection() {
    assertEffectIs("[fo|o]", "foo[fo|o]")
    assertEffectIs("[|foo]", "foo[|foo]")
    assertEffectIs("[foo|]", "foo[foo|]")
    assertEffectIs("moo[fo|o]bar", "moofoo[fo|o]bar")
    assertEffectIs("[|]", "[|]")
  }
}