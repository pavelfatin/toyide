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

import com.pavelfatin.toyide.editor.ColorScheme
import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.languages.toy.inspection._
import com.pavelfatin.toyide.languages.toy.parser.ProgramParser
import com.pavelfatin.toyide.{FileType, Language}

object ToyLanguage extends Language {
  def name = "Toy"

  def description = "C-like imperative language"

  def lexer = ToyLexer

  def parser = ProgramParser

  def colorings = Map(
    "Light" -> new ToyColoring(ColorScheme.LightColors),
    "Dark" -> new ToyColoring(ColorScheme.DarkColors))

  def complements = Seq((LBRACE, RBRACE), (LPAREN, RPAREN))

  def format = ToyFormat

  def comment = "//"

  def inspections = Seq(ReturnOutsideFunction, DuplicateIdentifier, UnresolvedReference,
    VoidValue, Applicability, TypeMismatch, OperatorApplication, IntegerRange, PrefixApplication,
    MissingReturn, UnreachableStatement, UnusedDeclaration, PredefinedIdentifier, Optimization, DivisionByZero)

  def adviser = ToyAdviser

  def fileType = FileType("Toy file", "toy")

  def examples = ToyExamples.Values
}