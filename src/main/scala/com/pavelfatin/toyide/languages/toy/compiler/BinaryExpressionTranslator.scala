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

package com.pavelfatin.toyide.languages.toy.compiler

import com.pavelfatin.toyide.languages.toy.node.BinaryExpression
import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.languages.toy.ToyType._
import com.pavelfatin.toyide.compiler.{Code, Labels}

trait BinaryExpressionTranslator extends ToyTranslatable { self: BinaryExpression =>
  override def translate(name: String, labels: Labels) = {
    val (leftNode, token, rightNode) = parts.getOrElse(
      interrupt("Incorrect binary expression: %s", span.text))

    val leftType = leftNode.nodeType.getOrElse(
      interrupt("Unknow left operand type: %s", span.text))

    val rightType = rightNode.nodeType.getOrElse(
      interrupt("Unknow right operand type: %s", span.text))

    val l = leftNode.translate(name, labels).instructions
    val r = rightNode.translate(name, labels).instructions

    lazy val l1 = labels.next()
    lazy val l2 = labels.next()
    lazy val l3 = labels.next()

    val s = (leftType, token.kind, rightType) match {
      case (IntegerType, PLUS, IntegerType) =>
        "%s%siadd\n".format(l, r)
      case (IntegerType, MINUS, IntegerType) =>
        "%s%sisub\n".format(l, r)
      case (IntegerType, STAR, IntegerType) =>
        "%s%simul\n".format(l, r)
      case (IntegerType, SLASH, IntegerType) =>
        "%s%sidiv\n".format(l, r)
      case (IntegerType, PERCENT, IntegerType) =>
        "%s%sirem\n".format(l, r)

      case (IntegerType, GT, IntegerType) =>
        "%s%sif_icmpgt %s\niconst_0\ngoto %s\n%s:\niconst_1\n%s:\n".format(l, r, l1, l2, l1, l2)
      case (IntegerType, GT_EQ, IntegerType) =>
        "%s%sif_icmpge %s\niconst_0\ngoto %s\n%s:\niconst_1\n%s:\n".format(l, r, l1, l2, l1, l2)
      case (IntegerType, LT, IntegerType) =>
        "%s%sif_icmplt %s\niconst_0\ngoto %s\n%s:\niconst_1\n%s:\n".format(l, r, l1, l2, l1, l2)
      case (IntegerType, LT_EQ, IntegerType) =>
        "%s%sif_icmple %s\niconst_0\ngoto %s\n%s:\niconst_1\n%s:\n".format(l, r, l1, l2, l1, l2)

      case (IntegerType, EQ_EQ, IntegerType) | (BooleanType, EQ_EQ, BooleanType) =>
        "%s%sif_icmpeq %s\niconst_0\ngoto %s\n%s:\niconst_1\n%s:\n".format(l, r, l1, l2, l1, l2)
      case (IntegerType, BANG_EQ, IntegerType) | (BooleanType, BANG_EQ, BooleanType) =>
        "%s%sif_icmpne %s\niconst_0\ngoto %s\n%s:\niconst_1\n%s:\n".format(l, r, l1, l2, l1, l2)

      case (StringType, EQ_EQ, StringType) =>
        "%s%sinvokevirtual java/lang/String.equals(Ljava/lang/Object;)Z\n".format(l, r)
      case (StringType, BANG_EQ, StringType) =>
        "%s%sinvokevirtual java/lang/String.equals(Ljava/lang/Object;)Z\nifne %s\niconst_1\ngoto %s\n%s:\niconst_0\n%s:\n".format(l, r, l1, l2, l1, l2)

      case (BooleanType, AMP_AMP, BooleanType) =>
        "%sifeq %s\n%sifeq %s\niconst_1\ngoto %s\n%s:\niconst_0\n%s:\n".format(l, l1, r, l1, l2, l1, l2)
      case (BooleanType, BAR_BAR, BooleanType) =>
        "%sifne %s\n%sifeq %s\n%s:\niconst_1\ngoto %s\n%s:\niconst_0\n%s:\n".format(l, l1, r, l2, l1, l3, l2, l3)

      case (StringType, PLUS, StringType) =>
        "%s%sinvokevirtual java/lang/String.concat(Ljava/lang/String;)Ljava/lang/String;\n".format(l, r)
      case (StringType, PLUS, IntegerType) =>
        "%s%sinvokestatic java/lang/Integer.toString(I)Ljava/lang/String;\ninvokevirtual java/lang/String.concat(Ljava/lang/String;)Ljava/lang/String;\n".format(l, r)
      case (StringType, PLUS, BooleanType) =>
        "%s%sinvokestatic java/lang/Boolean.toString(Z)Ljava/lang/String;\ninvokevirtual java/lang/String.concat(Ljava/lang/String;)Ljava/lang/String;\n".format(l, r)

      case _ => interrupt("Incorrect expression: %s", span.text)
    }

    Code(withLine(s))
  }
}