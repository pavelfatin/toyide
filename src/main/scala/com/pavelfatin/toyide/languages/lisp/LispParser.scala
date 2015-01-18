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

import com.pavelfatin.toyide.languages.lisp.LispTokens._
import com.pavelfatin.toyide.languages.lisp.node._
import com.pavelfatin.toyide.lexer.TokenKind
import com.pavelfatin.toyide.parser.{Parser, TreeBuilder}

object LispParser extends Parser {
  def parse(in: TreeBuilder) {
    in.capturing(new ProgramNode()) {
      while(!in.isEOF) expression(in)
    }
  }

  def expression(in: TreeBuilder) {
    if (in.matches(COMMENT)) {
      comment(in)
    } else if (matches(in, LPAREN)) {
      list(in, LPAREN, RPAREN)
    } else if (matches(in, LBRACKET)) {
      list(in, LBRACKET, RBRACKET)
    } else if (matches(in, INTEGER_LITERAL)) {
      integer(in)
    } else if(matches(in, BOOLEAN_LITERAL)) {
      boolean(in)
    } else if(matches(in, CHARACTER_LITERAL)) {
      character(in)
    } else if(matches(in, STRING_LITERAL)) {
      string(in)
    } else if(matches(in, PREDEFINED_SYMBOL, CUSTOM_SYMBOL)) {
      symbol(in)
    } else {
      in.error("Expression expected")
      if (!in.isEOF) in.advance()
    }
  }

  def comment(in: TreeBuilder) {
    in.capturing(new CommentNode()) {
      in.consume(COMMENT)
    }
  }

  def list(in: TreeBuilder, leftBound: TokenKind, rightBound: TokenKind) {
    in.capturing(new ListNode()) {
      in.grasp(Prefixes: _*)
      in.consume(leftBound)
      while (!in.matches(rightBound) && !in.isEOF) {
        if (in.matches(COMMA)) in.consume()
        expression(in)
      }
      in.consume(rightBound)
    }
  }

  def integer(in: TreeBuilder) {
    in.capturing(new IntegerLiteralNode()) {
      in.grasp(Prefixes: _*)
      in.consume(INTEGER_LITERAL)
    }
  }

  def boolean(in: TreeBuilder) {
    in.capturing(new BooleanLiteralNode()) {
      in.grasp(Prefixes: _*)
      in.consume(BOOLEAN_LITERAL)
    }
  }

  def character(in: TreeBuilder) {
    in.capturing(new CharacterLiteralNode()) {
      in.grasp(Prefixes: _*)
      in.consume(CHARACTER_LITERAL)
    }
  }

  def string(in: TreeBuilder) {
    in.capturing(new StringLiteralNode()) {
      in.grasp(Prefixes: _*)
      in.consume(STRING_LITERAL)
    }
  }

  def symbol(in: TreeBuilder) {
    in.capturing(new SymbolNode()) {
      in.grasp(Prefixes: _*)
      in.consume(CUSTOM_SYMBOL, PREDEFINED_SYMBOL)
    }
  }

  private def matches(in: TreeBuilder, kinds: TokenKind*): Boolean =
    in.matches(Prefixes: _*) && in.ahead(kinds: _*) || in.matches(kinds: _*)
}