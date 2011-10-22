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

package com.pavelfatin.toyide.languages.toy.format

import com.pavelfatin.toyide.languages.toy.parser._
import org.junit.Test

class EntityFormatTest extends FormatTestBase {
  @Test
  def typeSpec() {
    assertFormatted(":void", TypeSpecParser, ": void")
    assertFormatted(": void", TypeSpecParser, ": void")
    assertFormatted(":  void", TypeSpecParser, ": void")
  }

  @Test
  def typedIdent() {
    assertFormatted("foo:integer", ParameterParser, "foo: integer")
    assertFormatted("foo: integer", ParameterParser, "foo: integer")
    assertFormatted("foo:  integer", ParameterParser, "foo: integer")
    assertFormatted("foo :integer", ParameterParser, "foo: integer")
  }

  @Test
  def parameters() {
    assertFormatted("()", ParametersParser, "()")
    assertFormatted("(a: integer)", ParametersParser, "(a: integer)")
    assertFormatted("( a: integer)", ParametersParser, "(a: integer)")
    assertFormatted("(a: integer )", ParametersParser, "(a: integer)")
    assertFormatted("( a: integer )", ParametersParser, "(a: integer)")
    assertFormatted("(a: integer,b: integer)", ParametersParser, "(a: integer, b: integer)")
    assertFormatted("(a: integer, b: integer)", ParametersParser, "(a: integer, b: integer)")
    assertFormatted("(a: integer,  b: integer)", ParametersParser, "(a: integer, b: integer)")
    assertFormatted("(  a: integer,  b: integer  )", ParametersParser, "(a: integer, b: integer)")
  }

  @Test
  def block() {
    assertFormatted("{}", BlockParser, "{\n}")
  }

  @Test
  def function() {
    assertFormatted("def  foo(): void={\n}", FunctionParser, "def foo(): void = {\n}")
    assertFormatted("def foo(): void={\n}", FunctionParser, "def foo(): void = {\n}")
    assertFormatted("def foo(): void ={\n}", FunctionParser, "def foo(): void = {\n}")
    assertFormatted("def foo(): void  ={\n}", FunctionParser, "def foo(): void = {\n}")
    assertFormatted("def foo(): void= {\n}", FunctionParser, "def foo(): void = {\n}")
    assertFormatted("def foo(): void=  {\n}", FunctionParser, "def foo(): void = {\n}")
    assertFormatted("def foo(): void = {\n}", FunctionParser, "def foo(): void = {\n}")
    assertFormatted("def foo(): void  =  {\n}", FunctionParser, "def foo(): void = {\n}")
  }

  @Test
  def variable() {
    assertFormatted("var  foo: integer=1;", VariableParser, "var foo: integer = 1;")
    assertFormatted("var foo: integer=1 ;", VariableParser, "var foo: integer = 1;")
    assertFormatted("var foo: integer=1;", VariableParser, "var foo: integer = 1;")
    assertFormatted("var foo: integer =1;", VariableParser, "var foo: integer = 1;")
    assertFormatted("var foo: integer  =1;", VariableParser, "var foo: integer = 1;")
    assertFormatted("var foo: integer= 1;", VariableParser, "var foo: integer = 1;")
    assertFormatted("var foo: integer=  1;", VariableParser, "var foo: integer = 1;")
    assertFormatted("var foo: integer = 1;", VariableParser, "var foo: integer = 1;")
    assertFormatted("var foo: integer  =  1;", VariableParser, "var foo: integer = 1;")
  }

  @Test
  def returnStatement() {
    assertFormatted("return 1;", ReturnParser, "return 1;")
    assertFormatted("return  1;", ReturnParser, "return 1;")
    assertFormatted("return 1 ;", ReturnParser, "return 1;")
  }

  @Test
  def arguments() {
    assertFormatted("()", ArgumentsParser, "()")
    assertFormatted("(1)", ArgumentsParser, "(1)")
    assertFormatted("( 1)", ArgumentsParser, "(1)")
    assertFormatted("(1 )", ArgumentsParser, "(1)")
    assertFormatted("( 1 )", ArgumentsParser, "(1)")
    assertFormatted("(1,2)", ArgumentsParser, "(1, 2)")
    assertFormatted("(1, 2)", ArgumentsParser, "(1, 2)")
    assertFormatted("(1,  2)", ArgumentsParser, "(1, 2)")
    assertFormatted("(  1,  2  )", ArgumentsParser, "(1, 2)")
  }

  @Test
  def callExpression() {
    assertFormatted("foo()", CallExpressionParser, "foo()")
    assertFormatted("foo ()", CallExpressionParser, "foo()")
  }

  @Test
  def call() {
    assertFormatted("foo();", CallParser, "foo();")
    assertFormatted("foo() ;", CallParser, "foo();")
  }

  @Test
  def ifStatement() {
    assertFormatted("if(true) {\n}", IfParser, "if (true) {\n}")
    assertFormatted("if (true) {\n}", IfParser, "if (true) {\n}")
    assertFormatted("if  (true) {\n}", IfParser, "if (true) {\n}")
    assertFormatted("if (true){\n}", IfParser, "if (true) {\n}")
    assertFormatted("if (true)  {\n}", IfParser, "if (true) {\n}")
    assertFormatted("if (true) {\n}else{\n}", IfParser, "if (true) {\n} else {\n}")
    assertFormatted("if (true) {\n}  else {\n}", IfParser, "if (true) {\n} else {\n}")
    assertFormatted("if (true) {\n} else  {\n}", IfParser, "if (true) {\n} else {\n}")
    assertFormatted("if (true) {\n} else {\n}", IfParser, "if (true) {\n} else {\n}")
  }

  @Test
  def whileStatement() {
    assertFormatted("while(true) {\n}", WhileParser, "while (true) {\n}")
    assertFormatted("while (true) {\n}", WhileParser, "while (true) {\n}")
    assertFormatted("while  (true) {\n}", WhileParser, "while (true) {\n}")
    assertFormatted("while (true){\n}", WhileParser, "while (true) {\n}")
    assertFormatted("while (true)  {\n}", WhileParser, "while (true) {\n}")
  }

  @Test
  def assignment() {
    assertFormatted("foo=1;", AssignmentParser, "foo = 1;")
    assertFormatted("foo=1 ;", AssignmentParser, "foo = 1;")
    assertFormatted("foo =1;", AssignmentParser, "foo = 1;")
    assertFormatted("foo  =1;", AssignmentParser, "foo = 1;")
    assertFormatted("foo =1;", AssignmentParser, "foo = 1;")
    assertFormatted("foo = 1;", AssignmentParser, "foo = 1;")
    assertFormatted("foo =  1;", AssignmentParser, "foo = 1;")
  }

  @Test
  def comment() {
    assertFormatted("// some stuff", CommentParser, "// some stuff")
  }
}