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

package com.pavelfatin.toyide.languages.lisp.library

import org.junit.Test

class ListTest extends LibraryTestBase {
  @Test
  def nil() {
    assertValue("nil", "()")
  }

  @Test
  def count() {
    assertValue("(count nil)", "0")
    assertValue("(count '(1))", "1")
    assertValue("(count '(1 2))", "2")
    assertValue("(count '(1 2 3))", "3")
  }

  @Test
  def repeat() {
    assertValue("(repeat 0 7)", "()")
    assertValue("(repeat 1 7)", "(7)")
    assertValue("(repeat 3 7)", "(7 7 7)")

    assertValue("(repeat -1 7)", "()")
  }

  @Test
  def range() {
    assertValue("(range 0 0)", "()")
    assertValue("(range 0 1)", "(0)")
    assertValue("(range 0 3)", "(0 1 2)")
    assertValue("(range 2 5)", "(2 3 4)")

    assertValue("(range 1 0)", "()")
    assertValue("(range -3 -1)", "(-3 -2)")
  }

  @Test
  def nth() {
    assertValue("(nth '(1) 0)", "1")

    assertValue("(nth '(1 2 3) 0)", "1")
    assertValue("(nth '(1 2 3) 1)", "2")
    assertValue("(nth '(1 2 3) 2)", "3")
  }

  @Test
  def every() {
    assertValue("(every? even? '(2 4 6))", "true")

    assertValue("(every? even? '(1 4 6))", "false")
    assertValue("(every? even? '(2 5 6))", "false")
    assertValue("(every? even? '(2 4 7))", "false")
  }

  @Test
  def some() {
    assertValue("(some odd? '(2 4 6))", "()")

    assertValue("(some odd? '(1 4 6))", "true")
    assertValue("(some odd? '(2 5 6))", "true")
    assertValue("(some odd? '(2 4 7))", "true")

    assertValue("(some (fn [x] (if (> x 2) x)) '(2 4 6))", "4")
  }

  @Test
  def foldLeft() {
    assertValue("(fold-left (fn [x y] (+ x y)) 3 nil)", "3")
    assertValue("(fold-left (fn [x y] (+ x y)) 3 '(1))", "4")
    assertValue("(fold-left (fn [x y] (+ x y)) 3 '(1 2 4))", "10")

    assertValue("(fold-left (fn [acc x] (cons x acc)) nil '(1 2 3))", "(3 2 1)")
  }

  @Test
  def foldRight() {
    assertValue("(fold-right (fn [x y] (+ x y)) 3 nil)", "3")
    assertValue("(fold-right (fn [x y] (+ x y)) 3 '(1))", "4")
    assertValue("(fold-right (fn [x y] (+ x y)) 3 '(1 2 4))", "10")

    assertValue("(fold-right (fn [x acc] (cons x acc)) nil '(1 2 3))", "(1 2 3)")
  }

  @Test
  def reduceLeft() {
    assertValue("(reduce-left (fn [x y] (+ x y)) '(1))", "1")
    assertValue("(reduce-left (fn [x y] (+ x y)) '(1 2 4))", "7")

    assertValue("(reduce-left (fn [acc x] (cons x acc)) (list nil 1 2 3))", "(3 2 1)")

    assertError("(reduce-left (fn [x y] (+ x y)) nil)", "empty list")
  }

  @Test
  def reduceRight() {
    assertValue("(reduce-right (fn [x y] (+ x y)) '(1))", "1")
    assertValue("(reduce-right (fn [x y] (+ x y)) '(1 2 4))", "7")

    assertValue("(reduce-right (fn [x acc] (cons x acc)) (list 1 2 3 nil))", "(1 2 3)")

    assertError("(reduce-right (fn [x y] (+ x y)) nil)", "empty list")
  }

  @Test
  def reduce() {
    assertValue("(reduce + '(1 2 3))", "6")
    assertValue("(reduce + 2 '(1 2 3))", "8")
  }

  @Test
  def reverse() {
    assertValue("(reverse '(1 2 3))", "(3 2 1)")
  }

  @Test
  def concat() {
    assertValue("(concat '(1 2) '(3 4 5) '(7))", "(1 2 3 4 5 7)")
    assertValue("(concat)", "()")
  }

  @Test
  def last() {
    assertValue("(last '(1))", "1")
    assertValue("(last '(1 2 3))", "3")

    assertError("(last nil)", "empty list")
  }

  @Test
  def map() {
    assertValue("(map (fn [x] (+ x 2)) '(1 3 7))", "(3 5 9)")
  }

  @Test
  def mapAll() {
    assertValue("(map-all list '(1 3 7) '(2 4 8))", "((1 2) (3 4) (7 8))")
  }

  @Test
  def mapcat() {
    assertValue("(mapcat reverse '((3 2 1 0) (6 5 4) (9 8 7)))", "(0 1 2 3 4 5 6 7 8 9)")
    assertValue("(mapcat list '(1 2 3) '(4 5 6))", "(1 4 2 5 3 6)")
  }

  @Test
  def flatten() {
    assertValue("(flatten nil)", "()")
    assertValue("(flatten '(1))", "(1)")
    assertValue("(flatten '(1 2 3))", "(1 2 3)")
    assertValue("(flatten '((1) (2) (3)))", "(1 2 3)")
    assertValue("(flatten '((1 2 3) (4 5 6)))", "(1 2 3 4 5 6)")
    assertValue("(flatten '(((1 2) 3) (4 (5 6))))", "(1 2 3 4 5 6)")
  }

  @Test
  def filter() {
    assertValue("(filter (fn [x] (> x 2)) '(1 3 2 7))", "(3 7)")
  }

  @Test
  def separate() {
    assertValue("(separate (fn [x] (> x 0)) '(1 2 3 4))", "((1 2 3 4) ())")

    assertValue("(separate (fn [x] (< x 3)) '(1 2 3 4))", "((1 2) (3 4))")
    assertValue("(separate (fn [x] (> x 2)) '(1 2 3 4))", "((3 4) (1 2))")

    assertValue("(separate (fn [x] (> x 4)) '(1 2 3 4))", "(() (1 2 3 4))")
  }

  @Test
  def take() {
    assertValue("(take 0 '(1 2 3 4 5))", "()")
    assertValue("(take 1 '(1 2 3 4 5))", "(1)")
    assertValue("(take 3 '(1 2 3 4 5))", "(1 2 3)")
    assertValue("(take 4 '(1 2 3 4 5))", "(1 2 3 4)")
    assertValue("(take 5 '(1 2 3 4 5))", "(1 2 3 4 5)")

    assertValue("(take 6 '(1 2 3 4 5))", "(1 2 3 4 5)")
  }

  @Test
  def drop() {
    assertValue("(drop 0 '(1 2 3 4 5))", "(1 2 3 4 5)")
    assertValue("(drop 1 '(1 2 3 4 5))", "(2 3 4 5)")
    assertValue("(drop 3 '(1 2 3 4 5))", "(4 5)")
    assertValue("(drop 4 '(1 2 3 4 5))", "(5)")
    assertValue("(drop 5 '(1 2 3 4 5))", "()")

    assertValue("(drop 6 '(1 2 3 4 5))", "()")
  }

  @Test
  def takeLast() {
    assertValue("(take-last 1 nil)", "()")

    assertValue("(take-last 1 '(1 2 3))", "(3)")
    assertValue("(take-last 2 '(1 2 3))", "(2 3)")
    assertValue("(take-last 3 '(1 2 3))", "(1 2 3)")

    assertValue("(take-last 4 '(1 2 3))", "(1 2 3)")
  }

  @Test
  def dropLast() {
    assertValue("(drop-last 1 nil)", "()")

    assertValue("(drop-last 1 '(1 2 3))", "(1 2)")
    assertValue("(drop-last 2 '(1 2 3))", "(1)")
    assertValue("(drop-last 3 '(1 2 3))", "()")

    assertValue("(drop-last 4 '(1 2 3))", "()")
  }

  @Test
  def splitAt() {
    assertValue("(split-at 0 '(1 2 3 4 5))", "(() (1 2 3 4 5))")
    assertValue("(split-at 1 '(1 2 3 4 5))", "((1) (2 3 4 5))")
    assertValue("(split-at 2 '(1 2 3 4 5))", "((1 2) (3 4 5))")
    assertValue("(split-at 4 '(1 2 3 4 5))", "((1 2 3 4) (5))")
    assertValue("(split-at 5 '(1 2 3 4 5))", "((1 2 3 4 5) ())")

    assertValue("(split-at 6 '(1 2 3 4 5))", "((1 2 3 4 5) ())")
  }

  @Test
  def partition() {
    assertValue("(partition 1 nil)", "()")

    assertValue("(partition 1 '(1))", "((1))")
    assertValue("(partition 1 '(1 2))", "((1) (2))")

    assertValue("(partition 2 '(1 2 3 4))", "((1 2) (3 4))")
    assertValue("(partition 2 '(1 2 3 4 5))", "((1 2) (3 4) (5))")

    assertValue("(partition 5 '(1 2 3 4))", "((1 2 3 4))")
  }

  @Test
  def takeWhile() {
    assertValue("(take-while #(< % 3) nil)", "()")

    assertValue("(take-while #(< % 3) '(1 2 3 4 5))", "(1 2)")

    assertValue("(take-while #(< % 0) '(1 2 3 4 5))", "()")
    assertValue("(take-while #(< % 10) '(1 2 3 4 5))", "(1 2 3 4 5)")
  }

  @Test
  def dropWhile() {
    assertValue("(drop-while #(< % 3) nil)", "()")

    assertValue("(drop-while #(< % 3) '(1 2 3 4 5))", "(3 4 5)")

    assertValue("(drop-while #(< % 0) '(1 2 3 4 5))", "(1 2 3 4 5)")
    assertValue("(drop-while #(< % 10) '(1 2 3 4 5))", "()")
  }

  @Test
  def splitWith() {
    assertValue("(split-with (fn [_] true) nil)", "(() ())")
    assertValue("(split-with (fn [_] true) '(1 2 3))", "((1 2 3) ())")

    assertValue("(split-with (fn [_] false) nil)", "(() ())")
    assertValue("(split-with (fn [_] false) '(1 2 3))", "(() (1 2 3))")

    assertValue("(split-with #(= % 1) '(1 2 3))", "((1) (2 3))")
    assertValue("(split-with #(= % 2) '(1 2 3))", "(() (1 2 3))")
    assertValue("(split-with #(= % 3) '(1 2 3))", "(() (1 2 3))")

    assertValue("(split-with #(< % 4) '(1 2 3 4 5))", "((1 2 3) (4 5))")
  }

  @Test
  def partitionBy() {
    assertValue("(partition-by (fn [x] x) nil)", "()")
    assertValue("(partition-by (fn [x] 0) '(1 2 3))", "((1 2 3))")
    assertValue("(partition-by (fn [x] x) '(1 2 3))", "((1) (2) (3))")
    assertValue("(partition-by (fn [x] (/ x 3)) (range 1 10))", "((1 2) (3 4 5) (6 7 8) (9))")
  }

  @Test
  def zip() {
    assertValue("(zip nil nil)", "()")
    assertValue("(zip '(1) '(2))", "((1 2))")
    assertValue("(zip '(1 2 3) '(4 5 6 7))", "((1 4) (2 5) (3 6))")
    assertValue("(zip '(1 2 3 4) '(5 6 7))", "((1 5) (2 6) (3 7))")
  }

  @Test
  def zipAll() {
    assertValue("(zip-all '(1 2 3) '(4 5 6) '(7 8 9))", "((1 4 7) (2 5 8) (3 6 9))")
  }

  @Test
  def unzip() {
    assertValue("(unzip nil)", "(() ())")
    assertValue("(unzip '((1 2)))", "((1) (2))")
    assertValue("(unzip '((1 4) (2 5) (3 6)))", "((1 2 3) (4 5 6))")
  }

  @Test
  def firstIndexOf() {
    assertValue("(first-index-of (fn [_] true) nil)", "()")

    assertValue("(first-index-of (fn [_] true) '(1))", "0")
    assertValue("(first-index-of (fn [_] true) '(1 2 3))", "0")

    assertValue("(first-index-of (fn [_] false) '(1))", "()")
    assertValue("(first-index-of (fn [_] false) '(1 2 3))", "()")

    assertValue("(first-index-of #(= % 1) '(1 2 3))", "0")
    assertValue("(first-index-of #(= % 2) '(1 2 3))", "1")
    assertValue("(first-index-of #(= % 3) '(1 2 3))", "2")

    assertValue("(first-index-of #(= % 5) '(4 5 6))", "1")

    assertValue("(first-index-of #(= % 1) '(1 1 1))", "0")
  }

  @Test
  def split() {
    assertValue("(split (fn [_] true) nil)", "(())")
    assertValue("(split (fn [_] true) '(1))", "(() ())")
    assertValue("(split (fn [_] true) '(1 2 3))", "(() () () ())")

    assertValue("(split (fn [_] false) nil)", "(())")
    assertValue("(split (fn [_] false) '(1))", "((1))")
    assertValue("(split (fn [_] false) '(1 2 3))", "((1 2 3))")

    assertValue("(split #(= % 0) '(0))", "(() ())")
    assertValue("(split #(= % 0) '(0 0))", "(() () ())")

    assertValue("(split #(= % 0) '(1 0 2))", "((1) (2))")
    assertValue("(split #(= % 0) '(1 2 0 3 4 0 5))", "((1 2) (3 4) (5))")
  }

  @Test
  def join() {
    assertValue("(join nil nil)", "()")
    assertValue("(join nil '((1)))", "(1)")
    assertValue("(join nil '((1) (2) (3)))", "(1 2 3)")

    assertValue("(join '(0) nil)", "()")
    assertValue("(join '(0) '((1)))", "(1)")
    assertValue("(join '(0) '((1) (2) (3)))", "(1 0 2 0 3)")

    assertValue("(join '(0) '((1 2) (3 4)))", "(1 2 0 3 4)")

    assertValue("(join '(0 7) '((1 2) (3 4) (5)))", "(1 2 0 7 3 4 0 7 5)")

    assertValue("(join  '((1 2) (3 4)))", "(1 2 3 4)")
  }

  @Test
  def get() {
    assertValue("(get nil 1 \\z)", "\\z")

    assertValue("(get '(1) 1 \\z)", "\\z")

    assertValue("(get '(1 \\a) 1 \\z)", "\\a")

    assertValue("(get '(1 \\a 2 \\b) 1 \\z)", "\\a")
    assertValue("(get '(1 \\a 2 \\b) 2 \\z)", "\\b")

    assertValue("(get '(1 \\a 2 \\b) 3 \\z)", "\\z")

    assertValue("(get '(1 \\a 1 \\b) 1 \\z)", "\\a")
  }
}