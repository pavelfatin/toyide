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

package com.pavelfatin.toyide.editor

trait EditorActions {
  def complete: AnAction

  def copy: AnAction

  def cut: AnAction

  def duplicateLine: AnAction

  def escape: AnAction

  def format: AnAction

  def gotoDeclaration: AnAction

  def indentSelection: AnAction

  def moveLineDown: AnAction

  def moveLineUp: AnAction

  def optimize: AnAction

  def paste: AnAction

  def redo: AnAction

  def removeLine: AnAction

  def rename: AnAction

  def selectAll: AnAction

  def showUsages: AnAction

  def toggleLineComment: AnAction

  def undo: AnAction

  def unindentSelection: AnAction

  def all: Seq[AnAction] = List(
    complete,
    copy,
    cut,
    duplicateLine,
    escape,
    format,
    gotoDeclaration,
    indentSelection,
    moveLineDown,
    moveLineUp,
    optimize,
    paste,
    redo,
    removeLine,
    rename,
    selectAll,
    showUsages,
    toggleLineComment,
    format,
    undo,
    unindentSelection
  )
}