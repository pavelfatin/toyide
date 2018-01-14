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