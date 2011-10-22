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

abstract class ExampleTestBase {
  @Test
  def euler1() {
    assertOutput(ToyExamples.Euler1, "233168")
  }

  @Test
  def euler2() {
    assertOutput(ToyExamples.Euler2, "4613732")
  }

  @Test
  def euler6() {
    assertOutput(ToyExamples.Euler6, "25164150")
  }

  @Test
  def fibonacci() {
    assertOutput(ToyExamples.FibonacciNumbers, "0 1 1 2 3 5 8 13 21 34 55 89 144 233 377 610 987 1597 2584 4181 ")
  }

  @Test
  def primes() {
    assertOutput(ToyExamples.PrimeNumbers, "2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97 ")
  }

  @Test
  def multiplicationTable() {
    assertOutput(ToyExamples.MultiplicationTable,
""" 4  6  8 10 12 14 16 18
 6  9 12 15 18 21 24 27
 8 12 16 20 24 28 32 36
10 15 20 25 30 35 40 45
12 18 24 30 36 42 48 54
14 21 28 35 42 49 56 63
16 24 32 40 48 56 64 72
18 27 36 45 54 63 72 81
""".filter(_ != '\r').split("\n").map(_ + ' ').mkString("\n") + '\n')
  }

  protected def assertOutput(code: String, expected: String)
}