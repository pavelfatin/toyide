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