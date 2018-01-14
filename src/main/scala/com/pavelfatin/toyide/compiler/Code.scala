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