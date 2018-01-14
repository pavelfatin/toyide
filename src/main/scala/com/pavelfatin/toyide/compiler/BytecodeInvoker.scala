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