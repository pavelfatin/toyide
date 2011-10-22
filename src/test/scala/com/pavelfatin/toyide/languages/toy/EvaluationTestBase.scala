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

import org.junit.Test

abstract class EvaluationTestBase {
  @Test
  def callToPredefinedFunction() {
    assertOutput("print();", "")
    assertOutput("print(1);", "1")
    assertOutput("print(1, 2, 3);", "123")

    assertOutput("println();", "\n")
    assertOutput("println(1);", "1\n")
    assertOutput("println(1, 2, 3);", "123\n")
  }

  @Test
  def program() {
    assertOutput("", "")
    assertOutput("print(1);", "1")
    assertOutput("print(1); print(2); print(3);", "123")
  }

  @Test
  def variable() {
    assertOutput("var v: integer = 1;", "")
    assertOutput("var v: integer = 1; print(v);", "1")
  }

  @Test
  def variableInScope() {
    assertOutput("if (true) { var v: integer = 1; }", "")
    assertOutput("if (true) { var v: integer = 1; print(v); }", "1")
  }

  @Test
  def variableInScopes() {
    assertOutput("if (true) { var v: integer = 1; }; if (true) { var v: boolean = true; }", "")
    assertOutput("if (true) { var v: integer = 1; print(v); }; if (true) { var v: string = \"s\"; print(v); }", "1s")
  }

  @Test
  def variableInNestedScopes() {
    assertOutput("if (true) { var a: integer = 1; if (true) { var b: integer = 2; } }", "")
    assertOutput("if (true) { var a: integer = 1; if (true) { var b: integer = 2; print(b); }; print(a); }", "21")
  }

  @Test
  def variableInitializationMoment() {
    assertOutput("var a: integer = 1; a = 2; var b: integer = a; print(b);", "2")
  }

  @Test
  def localVariable() {
    assertOutput("def f(): void = { var v: integer = 1; }; f();", "")
    assertOutput("def f(): void = { var v: integer = 1; print(v); }; f();", "1")
  }

  @Test
  def localVariableInScope() {
    assertOutput("def f(): void = { if (true) { var v: integer = 1; } }; f();", "")
    assertOutput("def f(): void = { if (true) { var v: integer = 1; print(v); } }; f();", "1")
  }

  @Test
  def localVariableInScopes() {
    assertOutput("def f(): void = { if (true) { var v: integer = 1; }; if (true) { var v: boolean = true; } }; f();", "")
    assertOutput("def f(): void = { if (true) { var v: integer = 1; print(v); }; if (true) { var v: string = \"s\"; print(v); } }; f();", "1s")
  }

  @Test
  def localVariableInNestedScopes() {
    assertOutput("def f(): void = { if (true) { var a: integer = 1; if (true) { var b: integer = 2; } } }; f();", "")
    assertOutput("def f(): void = { if (true) { var a: integer = 1; if (true) { var b: integer = 2; print(b); }; print(a); } }; f();", "21")
  }

  @Test
  def localVariableInitializationMoment() {
    assertOutput("def f(): void = { var a: integer = 1; a = 2; var b: integer = a; print(b); }; f();", "2")
  }

  @Test
  def variableInitializerValueDuplication() {
    assertOutput("""
    var a: integer = 1;
    print(a);
    var b: integer = a;
    print(b);
    b = 2;
    print(a);
    print(b);
    """, "1112")
  }

  @Test
  def assignment() {
    assertOutput("var v: integer = 1; v = 2; print(v);", "2")
    assertOutput("var v: integer = 1; v = v + 2; print(v);", "3")
    assertOutput("def f(i: integer): void = { i = 2; print(i); }; f(1);", "2")
    assertOutput("def f(): void = { var v: integer = 1; v = 2; print(v); }; f();", "2")
  }

  @Test
  def assignmentExpressionValueDuplication() {
    assertOutput("""
    var a: integer = 1;
    print(a);
    var b: integer = 2;
    print(b);
    a = b;
    print(a);
    print(b);
    """, "1222")
  }

  @Test
  def valueInstances() {
    assertOutput("""
    var a: integer = 1;
    def f(): integer = {
      a = 2;
      return 0;
    }
    print(a + f());
    """, "1")

    assertOutput("""
    var a: integer = 1;
    def f(): integer = {
      a = 2;
      return 0;
    }
    print(f() + a);
    """, "2")
  }

  @Test
  def functionDeclaration() {
    assertOutput("def f(): void = { print(1); }", "")
  }

  @Test
  def functionCall() {
    assertOutput("def f(): void = { print(1); }; f();", "1")
  }

  @Test
  def functionBlock() {
    assertOutput("def f(): void = { print(1); print(2); print(3); }; f();", "123")
  }

  @Test
  def functionParameters() {
    assertOutput("def f(i: integer): void = { print(i); }; f(1);", "1")
    assertOutput("def f(a: integer, b: integer): void = { print(a, b); }; f(1, 2);", "12")
    assertOutput("def f(a: integer, b: string): void = { print(a, b); }; f(1, \"s\");", "1s")
  }

  @Test
  def functionParameterSource() {
    assertOutput("var i: integer = 1; def f(i: integer): void = { print(i); }; f(2);", "2")
    assertOutput("var i: integer = 1; def f(i: integer): void = {}; f(2); print(i);", "1")
  }

  @Test
  def functionParameterValueDuplication() {
    assertOutput("""
    var v: integer = 1;
    def f(p: integer): void = {
      print(p);
      p = 2;
      print(p);
    }
    f(v);
    print(v);
    """, "121")
  }

  @Test
  def functionResult() {
    assertOutput("def f(): integer = { return 1; }; print(f());", "1")
  }

  @Test
  def functionResultWithSideEffect() {
    assertOutput("def f(): integer = { print(1); return 2; }; print(f());", "12")
  }

  @Test
  def functionReturnBreaksEvaluation() {
    assertOutput("def f(): void = { return; print(1); }; f();", "")
    assertOutput("def f(): integer = { return 1; print(2); }; print(f());", "1")
  }

  @Test
  def functionReturnFromControlStatementBreaksEvaluation() {
    assertOutput("def f(): void = { if (true) { return; print(1); }; print(2); }; f();", "")
  }

  @Test
  def variableFromFunction() {
    assertOutput("var v: integer = 1; def f(): void = { print(v); }; f();", "1")
  }

  @Test
  def ifStatement() {
    assertOutput("if (true) { print(1); }", "1")
    assertOutput("if (false) { print(1); }", "")
  }

  @Test
  def ifStatementMultiple() {
    assertOutput(
      "if (true) { print(1); }" +
      "if (false) { print(2); }" +
      "if (true) { print(3); }" +
      "if (false) { print(4); }" +
      "if (true) { print(5); }",
      "135")
  }

  @Test
  def ifStatementBlock() {
    assertOutput("if (true) { print(1); print(2); print(3); }", "123")
  }

  @Test
  def ifStatementWithElse() {
    assertOutput("if (true) { print(1); } else { print(2); }", "1")
    assertOutput("if (false) { print(1); } else { print(2); }", "2")
  }

  @Test
  def elseStatementBlock() {
    assertOutput("if (false) {} else { print(1); print(2); print(3); }", "123")
  }

  @Test
  def whileStatement() {
    assertOutput("while (false) { print(42); }", "")
    assertOutput("var i: integer = 1; while (i <= 5) { print(i); i = i + 1; }", "12345")
  }

  @Test
  def recursiveFunction() {
    assertOutput("""
    def f(i: integer): void = {
      if (i > 0) {
        print(i);
        f(i - 1);
      }
    }
    f(5);
    """, "54321")

    assertOutput("""
    def f(i: integer): void = {
      if (i == 0) {
        return;
      };
      print(i);
      f(i - 1);
    }
    f(5);
    """, "54321")
  }

  @Test
  def recursiveFunctionWithResult() {
    assertOutput("""
    def f(i: integer): string = {
      if (i == 0) {
        return "";
      }
      return "" + i + f(i - 1);
    }
    print(f(5));
    """, "54321")
  }

  @Test
  def tailRecursiveFunction() {
    assertOutput("""
    def f(i: integer, s: string): void = {
      if (i == 0) {
        print(s);
        return;
      }
      f(i - 1, s + i);
    }
    f(5, "");
    """, "54321")
  }

  @Test
  def tailRecursiveFunctionWithResult() {
    assertOutput("""
    def f(i: integer, s: string): string = {
      if (i == 0) {
        return s;
      }
      return f(i - 1, s + i);
    }
    print(f(5, ""));
    """, "54321")
  }

  protected def assertOutput(code: String, expected: String)
}