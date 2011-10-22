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

class MoveLineUpTest extends ActionTestBase(new MoveLineUp(_, _)) {
  @Test
  def line() {
    assertEffectIs("foo\n|bar", "|bar\nfoo")

    assertEffectIs("foo\n|bar\n", "|bar\nfoo\n")
    assertEffectIs("foo\n|bar\nmoo", "|bar\nfoo\nmoo")

    assertEffectIs("\nfoo\n|bar", "\n|bar\nfoo")
    assertEffectIs("moo\nfoo\n|bar", "moo\n|bar\nfoo")

    assertEffectIs("\nfoo\n|bar\n", "\n|bar\nfoo\n")
    assertEffectIs("moo\nfoo\n|bar\nmoo", "moo\n|bar\nfoo\nmoo")

    assertEffectIs("\n|foo", "|foo\n")
    assertEffectIs("foo\n|", "|\nfoo")
  }
}