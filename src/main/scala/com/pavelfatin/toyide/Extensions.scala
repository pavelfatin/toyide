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

import collection.generic.CanBuildFrom
import scala.reflect.{classTag, ClassTag}

object Extensions {
  import language.higherKinds

  private type CanBuildTo[Elem, CC[X]] = CanBuildFrom[Nothing, Elem, CC[Elem]]

  implicit class RichTraversable[CC[X] <: Traversable[X], A](val value: CC[A]) extends AnyVal {
    def filterBy[T](implicit m: ClassTag[T], cbf: CanBuildTo[T, CC]): CC[T] =
      value.filter(classTag[T].runtimeClass.isInstance(_)).map[T, CC[T]](_.asInstanceOf[T])(collection.breakOut)

    def findBy[T: ClassTag]: Option[T] =
      value.find(classTag[T].runtimeClass.isInstance(_)).map(_.asInstanceOf[T])

    def collectAll[B](pf: PartialFunction[A, B])(implicit cbf: CanBuildTo[B, CC]): Option[CC[B]] = {
      if (value.forall(pf.isDefinedAt)) Some(value.collect(pf)(collection.breakOut)) else None
    }
  }

  implicit class RichCharSequence(val chars: CharSequence) extends AnyVal {
    def count(p: Char => Boolean): Int = {
      var i = 0
      var n = 0
      while (i < chars.length) {
        if (p(chars.charAt(i))) n += 1
        i += 1
      }
      n
    }

    def take(n: Int): CharSequence = {
      chars.subSequence(0, n)
    }

    def subSequence(begin: Int): CharSequence = {
      chars.subSequence(begin, chars.length)
    }
  }
}