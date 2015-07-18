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

import com.pavelfatin.toyide.Example

object ToyExamples {
 val Euler1 = """
// Project Euler - Problem 1

// The sum of all the multiples of 3 or 5 below 1000.

var sum: integer = 0;

var i: integer = 0;

while (i < 1000) {
  if (i % 3 == 0 || i % 5 == 0) {
    sum = sum + i;
  }
  i = i + 1;
}

print(sum);
"""

  val Euler2 = """
// Project Euler - Problem 2

// The sum of the even-valued terms in the Fibonacci
// sequence whose values do not exceed four million.

var a: integer = 0;
var b: integer = 1;

var sum: integer = 0;

while (a <= 4000000) {
  if (a % 2 == 0) {
    sum = sum + a;
  }
  var t: integer = a;
  a = b;
  b = t + b;
}

print(sum);
"""

  val Euler6 = """
// Project Euler - Problem 6

// The difference between the sum of the squares of the first
// one hundred natural numbers and the square of the sum.

var sum: integer = 0;
var sumOfSquares: integer = 0;

var i: integer = 1;

while (i <= 100) {
  var square: integer = i * i;
  sum = sum + i;
  sumOfSquares = sumOfSquares + square;
  i = i + 1;
}

print(sum * sum - sumOfSquares);
"""

  val FibonacciNumbers = """
// The first twenty terms in the Fibonacci sequence.

def fib(a: integer, b: integer, c: integer): void = {
  if (c > 0) {
    print(a, " ");
    fib(b, a + b, c - 1);
  }
}

fib(0, 1, 20);
"""

  val PrimeNumbers =
"""
// The prime numbers whose values are less than one hundred.

def isDivisible(n: integer, a: integer, b: integer): boolean = {
  var i: integer = a;

  while (i * i <= b) {
    if (n % i == 0) {
      return true;
    }
    i = i + 1;
  }

  return false;
}

var i: integer = 2;

while (i < 100) {
  if (!isDivisible(i, 2, i)) {
    print(i, " ");
  }
  i = i + 1;
}
"""

  val MultiplicationTable = """
// Prints the multiplication table.

var i: integer = 2;

while (i < 10) {
  var j: integer = 2;

  while (j < 10) {
    var k: integer = i * j;
    if (k < 10) {
      print(" ");
    }
    print(k, " ");
    j = j + 1;
  }

  println();
  i = i + 1;
}
"""

  val HighlightingDemo = """
// Highlighting demo (try different color schemes)

// Unused variable
var name: string = "foo";

// Expression type mismatch
while (12345) {}

// Expression can be simplified
print(4 * 10 + 2);

// Function cannot be resolved
unknown(true);
"""

  val Exception = """
// Runtime exception and stack trace demo.

def c(): void = {
  println(1 / 0);
}

def b(): void = {
  c();
}

def a(): void = {
  b();
}

a();
"""

  val StackOverflow = """
// Stack overflow demo.

def foo(): void = {
  foo();
}

foo();
"""

  val Values = Seq(
    Example("Project Euler 1", '1', Euler1),
    Example("Project Euler 2", '2', Euler2),
    Example("Project Euler 6", '6', Euler6),
    Example("Fibonacci Numbers", 'F', FibonacciNumbers),
    Example("Prime Numbers", 'P', PrimeNumbers),
    Example("Highlighting Demo", 'H', HighlightingDemo),
    Example("Exception", 'E', Exception),
    Example("Stack Overflow", 'S', StackOverflow))
}