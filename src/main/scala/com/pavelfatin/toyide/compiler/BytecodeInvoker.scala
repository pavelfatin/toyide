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

import java.lang.reflect.InvocationTargetException
import java.io.{OutputStream, PrintStream}
import com.pavelfatin.toyide.Output

object BytecodeInvoker {
  @throws(classOf[InvocationException])
  def invoke(code: Array[Byte], name: String, output: Output) {
    val loader = new DynamicClassLoader()

    val mainClass = loader.define(name, code)
    val constructor = mainClass.getConstructor(classOf[PrintStream])
    val instance = constructor.newInstance(new PrintStream(new OutputAdapter(output)));
    val mainMethod = mainClass.getMethod("run", classOf[Array[String]]);

    try {
      mainMethod.invoke(instance, Array[String]());
    } catch {
      case e: InvocationTargetException =>
        val cause = e.getCause
        val places = cause.getStackTrace.filter(it => it.getClassName == name).map { it =>
          val method = it.getMethodName
          val line = it.getLineNumber
          if (method == "run") Place(None, line) else Place(Some(method), line)
        }
        throw new InvocationException(cause.toString, places)
    }
  }

  private class DynamicClassLoader extends ClassLoader {
    def define(className: String, bytecode: Array[Byte]) = {
      super.defineClass(className, bytecode, 0, bytecode.length);
    }
  }

  private class OutputAdapter(output: Output) extends OutputStream {
    def write(b: Int) {
      output.print(b.toChar.toString)
    }
  }
}