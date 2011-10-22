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

package com.pavelfatin.toyide.compiler

case class Code(instructions: String = "", fields: String = "", methods: String = "") {
  private val Template = """
.class public %s
.super java/lang/Object

.field private out Ljava/io/PrintStream;
%s

.method public <init>(Ljava/io/PrintStream;)V
  .limit stack 2
  .limit locals 2

   aload_0
   invokespecial java/lang/Object/<init>()V

   aload_0
   aload_1
   putfield %1$s/out Ljava/io/PrintStream;

   return
.end method

%s

.method public run([Ljava/lang/String;)V
   .limit stack 10
   .limit locals 10

   %s

   return
.end method

.method public static main([Ljava/lang/String;)V
   .limit stack 3
   .limit locals 1

   new %1$s
   dup
   getstatic java/lang/System/out Ljava/io/PrintStream;
   invokespecial %1$s/<init>(Ljava/io/PrintStream;)V

   aload_0
   invokevirtual %1$s/run([Ljava/lang/String;)V

   return
.end method
  """

  def +(other: Code) = {
    Code("%s\n%s".format(instructions, other.instructions),
      "%s\n%s".format(fields, other.fields),
      "%s\n%s".format(methods, other.methods))
  }

  def toText(name: String): String = Template.format(name, fields, methods, instructions)
    .replace("\r", "")
    .replaceAll("(?m)^\\s+$", "")
    .replaceAll("\n{2,}", "\n\n")
    .replaceAll(".line (\\d+)(?:\n.line (\\1))+", ".line \\1")
}