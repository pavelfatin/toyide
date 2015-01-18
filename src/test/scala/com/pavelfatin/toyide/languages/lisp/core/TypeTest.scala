/*
 * Copyright (C) 2014 Pavel Fatin <http://pavelfatin.com>
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

package com.pavelfatin.toyide.languages.lisp.core

import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
import org.junit.Test

class TypeTest extends InterpreterTesting {
  @Test
  def integer() {
    assertValue("(integer? 1)", "true")
    assertValue("(integer? \\с)", "false")
  }

  @Test
  def boolean() {
    assertValue("(boolean? false)", "true")
    assertValue("(boolean? 1)", "false")
  }

  @Test
  def character() {
    assertValue("(character? \\c)", "true")
    assertValue("(character? 1)", "false")
  }

  @Test
  def symbol() {
    assertValue("(symbol? 'do)", "true")
    assertValue("(symbol? 1)", "false")
  }

  @Test
  def function() {
    assertValue("(function? do)", "true")
    assertValue("(function? 1)", "false")
  }

  @Test
  def list() {
    assertValue("(list? (list))", "true")
    assertValue("(list? 1)", "false")
  }
}