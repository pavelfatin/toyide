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

package com.pavelfatin.toyide.languages.toy

import com.pavelfatin.toyide.editor.AdviserTestBase
import com.pavelfatin.toyide.languages.toy.parser.ProgramParser
import org.junit.Test

class ToyAdviserTest extends AdviserTestBase(ToyLexer, ProgramParser, ToyAdviser) {
  @Test
  def insideEmptyProgram() {
    assertVariantsAre("|")("print", "println", "def", "var", "if", "while")
  }

  @Test
  def atIdentifier() {
    assertVariantsAre("def |")()
    assertVariantsAre("def foo|")()
    assertVariantsAre("def |foo")()
    assertVariantsAre("def foo|bar")()

    assertVariantsAre("var |")()
    assertVariantsAre("var foo|")()
    assertVariantsAre("var |foo")()
    assertVariantsAre("var foo|bar")()
  }

  @Test
  def afterKeyword() {
    assertVariantsAre("if |")()
    assertVariantsAre("while |")()
  }

  @Test
  def afterIf() {
    assertVariantsAre("if (true) {} |")("print", "println", "def", "var", "else", "if", "while")
    assertVariantsAre("if (true) {} else {} |")("print", "println", "def", "var", "if", "while")
  }

  @Test
  def insideControlStructure() {
    assertVariantsAre("if (true) {|}")("print", "println", "if", "while")
  }

  @Test
  def afterIfInsideControlStructure() {
    assertVariantsAre("if (true) { if (true) {} |}")("print", "println", "else", "if", "while")
  }

  @Test
  def insideFunction() {
    assertVariantsAre("def f(): void = {|}")("f", "print", "println", "return", "if", "while")
    assertVariantsAre("def f(): void = { if (true) {|} }")("f", "print", "println", "return", "if", "while")
  }

  @Test
  def insideTypeSpecifier() {
    assertVariantsAre("var v:|")("string", "integer", "boolean")
    assertVariantsAre("var v:| = true;")("string", "integer", "boolean")

    assertVariantsAre("def f(p:|")("string", "integer", "boolean")
    assertVariantsAre("def f(p:|): void = {}")("string", "integer", "boolean")

    assertVariantsAre("def f():|")("void", "string", "integer", "boolean")
    assertVariantsAre("def f():| = {}")("void", "string", "integer", "boolean")
  }

  @Test
  def insideExpression() {
    assertVariantsAre("if (|")("true", "false")
    assertVariantsAre("if (|) {}")("true", "false")
  }

  @Test
  def insideArguments() {
    assertVariantsAre("f(|")("true", "false")
    assertVariantsAre("f(|);")("true", "false")
  }

  @Test
  def variable() {
    assertVariantsAre("var a: integer = 1;|")("a", "print", "println", "def", "var", "if", "while")
    assertVariantsAre("|;var a: integer = 1")("print", "println", "def", "var", "if", "while")
  }

  @Test
  def function() {
    assertVariantsAre("def f(): void = {};|")("f", "print", "println", "def", "var", "if", "while")
    assertVariantsAre("def f(): void = {|}")("f", "print", "println", "return", "if", "while")
    assertVariantsAre("|;def f(): void = {}")("print", "println", "def", "var", "if", "while")
  }

  @Test
  def parameter() {
    assertVariantsAre("def f(p: integer): void = {|}")("p", "f", "print", "println", "return", "if", "while")
    assertVariantsAre("def f(p: integer): void = {};|")("f", "print", "println", "def", "var", "if", "while")
  }

  @Test
  def insideBinaryExpression() {
    assertVariantsAre("if (true && |")("true", "false")
    assertVariantsAre("if (true && |) {}")("true", "false")
  }

  @Test
  def insidePrefixExpression() {
    assertVariantsAre("if (!|")("true", "false")
    assertVariantsAre("if (!|) {}")("true", "false")
  }

  @Test
  def referenceInsideHolders() {
    assertVariantsAre("var a: integer = 1; if (|")("a", "true", "false")
    assertVariantsAre("var a: integer = 1; foo(|")("a", "true", "false")
    assertVariantsAre("var a: integer = 1; var b:|")("string", "integer", "boolean")
    assertVariantsAre("var a: integer = 1; if (2 + |")("a", "true", "false")
  }

  @Test
  def sorting() {
    assertVariantsAre("def f(a: integer, b: integer): void = { if (|) }")("a", "b", "f", "true", "false")
    assertVariantsAre("def f(b: integer, a: integer): void = { if (|) }")("a", "b", "f", "true", "false")

    assertVariantsAre("def a(): void = {}; def b(): void = {}; if (|)")("a", "b", "true", "false")
    assertVariantsAre("def b(): void = {}; def a(): void = {}; if (|)")("a", "b", "true", "false")

    assertVariantsAre("var a: integer = 1; var b: integer = 2; if (|)")("a", "b", "true", "false")
    assertVariantsAre("var b: integer = 2; var a: integer = 1; if (|)")("a", "b", "true", "false")
  }

  @Test
  def order() {
    assertVariantsAre("var a: integer = 1; def b(): void = {}; def f(c: integer): void = { if (|) }")("c", "b", "f", "a", "true", "false")
  }

  @Test
  def duplicates() {
    assertVariantsAre("var v: integer = 1; var v: integer = 1; if (|)")("v", "true", "false")
    assertVariantsAre("var v: integer = 1; def f(v: integer): void = { if (|) }")("v", "f", "true", "false")
  }

  @Test
  def variableItself() {
    assertVariantsAre("var a: integer = |")("true", "false")
    assertVariantsAre("var a: integer = 1; var b: integer = |")("a", "true", "false")
  }
}