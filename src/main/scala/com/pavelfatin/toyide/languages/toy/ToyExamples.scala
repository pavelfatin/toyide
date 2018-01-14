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