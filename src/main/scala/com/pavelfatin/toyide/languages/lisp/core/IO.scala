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

package com.pavelfatin.toyide.languages.lisp.core

import java.io.{File, IOException}
import java.net.{ServerSocket, Socket, SocketException}
import java.util.concurrent.SynchronousQueue

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.languages.lisp.value._

import scala.annotation.tailrec

abstract class IOFunction(name: String) extends CoreFunction(name) {
  final def apply(arguments: Seq[Expression], environment: Environment, output: Output) = {
    try {
      apply0(arguments, environment, output)
    } catch {
      case e: IOException => error(e.getMessage, environment)
    }
  }

  protected def apply0(arguments: Seq[Expression], environment: Environment, output: Output): Expression
}

object Dir extends IOFunction("dir") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(StringValue(path)) =>
      val files = Option(new File(path).listFiles).map(_.toSeq).getOrElse(Seq.empty)
      ListValue(files.map(file => StringValue(file.getName)))
    case _ => expected("path", arguments, environment)
  }
}

object Exists extends IOFunction("exists?") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(StringValue(filename)) =>
      BooleanValue(new File(filename).exists)
    case _ => expected("filename", arguments, environment)
  }
}

object Directory extends IOFunction("directory?") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(StringValue(filename)) =>
      BooleanValue(new File(filename).isDirectory)
    case _ => expected("filename", arguments, environment)
  }
}

object Open extends IOFunction("open") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(StringValue(filename)) =>
      new FileHandleValue(new File(filename))
    case _ => expected("filename", arguments, environment)
  }
}

object Listen extends IOFunction("listen") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(IntegerValue(port), f: FunctionValue) =>
      val serverSocket = new ServerSocket(port)
      closeOnStopping(Thread.currentThread(), serverSocket)

      val sockets = new SynchronousQueue[Socket](true)
      acceptConnections(serverSocket, sockets)

      @tailrec
      def handleConnection() {
        try {
          val socket = sockets.take()
          val socketHandle = new SocketHandleValue(socket)
          f.apply(Seq(socketHandle), environment, output)
        } catch {
          case _: IllegalMonitorStateException => return
        }
        handleConnection()
      }

      handleConnection()

      ListValue.Empty

    case _ => expected("port f", arguments, environment)
  }

  private def closeOnStopping(thread: Thread, serverSocket: ServerSocket) {
    val runnable = new Runnable() {
      override def run() {
        thread.synchronized(thread.join())
        serverSocket.close()
      }
    }
    new Thread(runnable).start()
  }

  private def acceptConnections(serverSocket: ServerSocket, queue: SynchronousQueue[Socket]) {
    val runnable = new Runnable() {
      @tailrec
      def run() {
        try {
          val socket = serverSocket.accept()
          queue.put(socket)
        } catch {
          case _: SocketException => return
        }
        run()
      }
    }
    new Thread(runnable).start()
  }
}

object Read extends IOFunction("read") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(handle: HandleValue) => read(handle, None)
    case Seq(handle: HandleValue, CharacterValue(terminator)) => read(handle, Some(terminator))
    case _ => expected("handle terminator?", arguments, environment)
  }

  private def read(handle: HandleValue, terminator: Option[Char]) = {
    val values = handle.read(terminator).map(CharacterValue)
    ListValue(values)
  }
}

object Write extends IOFunction("write") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(handle: HandleValue, StringValue(s)) =>
      handle.write(s.toSeq)
      ListValue.Empty
    case _ => expected("handle string", arguments, environment)
  }
}

object Flush extends IOFunction("flush") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(handle: HandleValue) =>
      handle.flush()
      ListValue.Empty
    case _ => expected("handle", arguments, environment)
  }
}

object Close extends IOFunction("close") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output) = arguments match {
    case Seq(handle: HandleValue) =>
      handle.close()
      ListValue.Empty
    case _ => expected("handle", arguments, environment)
  }
}
