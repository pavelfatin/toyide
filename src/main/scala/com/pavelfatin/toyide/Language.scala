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

package com.pavelfatin.toyide

import editor.{Adviser, Coloring}
import formatter.Format
import inspection.Inspection
import lexer.{TokenKind, Lexer}
import parser.Parser

trait Language {
  def lexer: Lexer

  def parser: Parser

  def coloring: Coloring

  def complements: Seq[(TokenKind, TokenKind)]

  def format: Format

  def comment: String

  def inspections: Seq[Inspection]

  def adviser: Adviser

  def fileType: FileType

  def examples: Seq[Example]
}