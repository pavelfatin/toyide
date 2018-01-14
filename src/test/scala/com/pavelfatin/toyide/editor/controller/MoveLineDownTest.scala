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

package com.pavelfatin.toyide.editor.controller

import org.junit.Test

class MoveLineDownTest extends ActionTestBase(new MoveLineDown(_, _)) {
  @Test
  def line() {
    assertEffectIs("|foo\nbar", "bar\n|foo")

    assertEffectIs("|foo\nbar\n", "bar\n|foo\n")
    assertEffectIs("|foo\nbar\nmoo", "bar\n|foo\nmoo")

    assertEffectIs("\n|foo\nbar", "\nbar\n|foo")
    assertEffectIs("moo\n|foo\nbar", "moo\nbar\n|foo")

    assertEffectIs("\n|foo\nbar\n", "\nbar\n|foo\n")
    assertEffectIs("moo\n|foo\nbar\nmoo", "moo\nbar\n|foo\nmoo")

    assertEffectIs("|\nfoo", "foo\n|")
    assertEffectIs("|foo\n", "\n|foo")
  }
}