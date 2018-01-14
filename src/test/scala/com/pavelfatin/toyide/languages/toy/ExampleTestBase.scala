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