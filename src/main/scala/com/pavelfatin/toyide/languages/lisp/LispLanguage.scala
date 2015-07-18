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

package com.pavelfatin.toyide.languages.lisp

import com.pavelfatin.toyide.editor.ColorScheme
import com.pavelfatin.toyide.{FileType, Language}

object LispLanguage extends Language {
  def name = "Lisp"

  def description = "Clojure-like functional language"

  def lexer = LispLexer

  def parser = LispParser

  def colorings = Map(
    "Light" -> new LispColoring(ColorScheme.LightColors),
    "Dark" -> new LispColoring(ColorScheme.DarkColors))

  def complements = Seq(LispTokens.Parens, LispTokens.Brackets)

  def format = LispFormat

  def comment = ";"

  def inspections = Seq()

  def adviser = LispAdviser

  def fileType = FileType("Lisp file", "lisp")

  def examples = LispExamples.Values
}