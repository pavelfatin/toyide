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
      new ImmediateTextPainter(context, lexer),
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
