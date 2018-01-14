/*
 *
 *  * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.pavelfatin.toyide.editor.painter

import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.editor._
import com.pavelfatin.toyide.lexer.Lexer

object PainterFactory {
  def createPainters(document: Document, terminal: Terminal, data: Data, canvas: Canvas, grid: Grid, lexer: Lexer,
                     matcher: BraceMatcher, errors: ErrorHolder, coloring: Coloring, processor: ActionProcessor): Seq[Painter] = {

    val context = PainterContext(document, terminal, data, canvas, grid, coloring)

    val errorPainter = new ErrorPainter(context, errors)

    val selectionPainter = new SelectionPainter(context)

    val hoverPainter = new HoverPainter(context)

    val painters = Seq(
      new ImmediateTextPainter(context, lexer, processor),
      new BackgroundPainter(context),
      new CurrentLinePainter(context),
      errorPainter,
      new MatchPainter(context, matcher, processor),
      new HighlightPainter(context),
      hoverPainter,
      selectionPainter,
      new TextPainter(context, lexer, Seq(errorPainter, hoverPainter, selectionPainter)),
      new CaretPainter(context))

    painters
  }
}
