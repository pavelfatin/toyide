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

package com.pavelfatin.toyide.editor

import java.awt._
import java.awt.event._
import javax.swing.border.EmptyBorder
import javax.swing.{Renderer => _, _}

import com.pavelfatin.toyide.Interval
import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.editor.controller.{Controller, ControllerImpl}
import com.pavelfatin.toyide.editor.painter._
import com.pavelfatin.toyide.formatter.{Format, FormatterImpl}
import com.pavelfatin.toyide.lexer.Lexer

private class EditorImpl(val document: Document, val data: Data, val holder: ErrorHolder,
                         lexer: Lexer, coloring: Coloring, matcher: BraceMatcher,
                         format: Format, adviser: Adviser, listRenderer: ListCellRenderer,
                         comment: String, history: History) extends Editor {

  private val grid = new Grid(new Dimension(8, 20), Pane.getInsets)

  private val NormalFont = new Font(coloring.fontFamily, Font.PLAIN, coloring.fontSize)

  private val TabSize = 2

  private lazy val renderingHints = Option(Toolkit.getDefaultToolkit.getDesktopProperty("awt.font.desktophints"))

  private val controller: Controller =
    new ControllerImpl(document, data, terminal, grid, adviser, new FormatterImpl(format), TabSize, comment, history)

  private val scroll = new JScrollPane(Pane)

  private val canvas = new CanvasImpl(Pane, scroll)

  val component = {
    val stripe = new Stripe(document, data, holder, grid, canvas)
    stripe.onChange { y =>
      val point = toPoint(terminal.offset)
      terminal.offset = document.toNearestOffset(grid.toLocation(new Point(point.x, y)))
      val h = Pane.getVisibleRect.height
      Pane.scrollRectToVisible(new Rectangle(0, y - h / 2, 0, h))
      Pane.requestFocusInWindow()
    }
    val panel = new JPanel(new BorderLayout())
    val map = scroll.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    map.allKeys().foreach(map.put(_, "none"))
    map.put(KeyStroke.getKeyStroke("ctrl pressed UP"), "unitScrollUp")
    map.put(KeyStroke.getKeyStroke("ctrl pressed DOWN"), "unitScrollDown")
    map.put(KeyStroke.getKeyStroke("ctrl pressed PAGE_UP"), "scrollUp")
    map.put(KeyStroke.getKeyStroke("ctrl pressed PAGE_DOWN"), "scrollDown")
    panel.add(scroll, BorderLayout.CENTER)
    panel.add(stripe, BorderLayout.EAST)
    swing.Component.wrap(panel)
  }

  def actions = controller.actions

  def pane = swing.Component.wrap(Pane)

  def text = document.text

  def text_=(s: String) {
    history.recording(document, terminal) {
      terminal.offset = 0
      terminal.selection = None
      terminal.highlights = Seq.empty
      document.text = s
    }
  }

  private var _message: Option[String] = None

  def message = _message

  private def message_=(m: Option[String]) {
    if (_message != m) {
      _message = m
      notifyObservers()
    }
  }

  def dispose() {
    tooltipHandler.dispose()
    timer.stop()
  }

  def terminal: Terminal = MyTerminal

  document.onChange { _ =>
    val size = grid.toSize(document.linesCount, document.maximumIndent)
    if (Pane.getPreferredSize != size) {
      Pane.setPreferredSize(size)
      Pane.revalidate()
    }
  }

  terminal.onChange {
    case CaretMovement(from, to) =>
      scrollToOffsetVisible(to)
      updateMessage()
    case HighlightsChange(from, to) =>
      to.headOption.foreach(it => scrollToOffsetVisible(it.begin))
    case _ =>
  }

  holder.onChange { _ =>
    updateMessage()
  }

  private def updateMessage() {
    message = errorAt(terminal.offset).map(_.message)
  }

  private var popupVisible = false

  private def toPoint(offset: Int): Point = grid.toPoint(document.toLocation(offset))

  private val tooltipHandler = new TooltipHandler(Pane,
    point => document.toOffset(grid.toLocation(point)).flatMap(errorAt))

  private val timer = new Timer(500, new ActionListener() {
    def actionPerformed(e: ActionEvent) {
      if(shouldDisplayCaret) {
        canvas.caretVisible = !canvas.caretVisible
      }
    }
  })

  private def shouldDisplayCaret = Pane.isFocusOwner || popupVisible

  private val painters = PainterFactory.createPainters(document, terminal, data,
    canvas, grid, lexer, matcher, holder, coloring, controller)

  painters.foreach(painter => painter.onChange(handlePaintingRequest(painter, _)))

  private var immediatePainting = false

  private val handlePaintingRequest = (painter: Painter, rectangle: Rectangle) => {
    if (canvas.visible) {
      val visibleRectangle = rectangle.intersection(canvas.visibleRectangle)

      if (!visibleRectangle.isEmpty) {
        if (painter.immediate) {
          immediatePainting = true
          Pane.paintImmediately(visibleRectangle)
          immediatePainting = false
        } else {
          immediatePainting = false
          Pane.repaint(visibleRectangle)
        }
      }
    }
  }

  timer.start()

  // handle external changes
  document.onChange { event =>
    terminal.offset = terminal.offset.min(document.length)
    val selection = terminal.selection.map(it => Interval(it.begin.min(document.length), it.end.min(document.length)))
    terminal.selection = selection.filterNot(_.empty)
    terminal.highlights = Seq.empty
  }

  private def scrollToOffsetVisible(offset: Int) {
    val h = grid.cellSize.height
    val w = grid.cellSize.width
    val p = toPoint(offset)

    val spot = {
      val panelBounds = Pane.getBounds(null)
      panelBounds.setLocation(0, 0)
      panelBounds.intersection(new Rectangle(p.x - w * 2, p.y - h, w * 4, h * 3))
    }

    if (!scroll.getViewport.getViewRect.contains(spot)) {
      Pane.scrollRectToVisible(spot)
    }
  }

  private def updateCaret() {
    canvas.caretVisible = shouldDisplayCaret
    timer.restart()
  }

  private def errorAt(offset: Int): Option[Error] = {
    val errors = holder.errors.filter(_.interval.withEndShift(1).includes(offset))
    errors.sortBy(!_.fatal).headOption
  }

  Pane.addKeyListener(new KeyAdapter() {
    override def keyPressed(e: KeyEvent) {
      controller.processKeyPressed(e)
      updateCaret()
    }

    override def keyTyped(e: KeyEvent) {
      controller.processKeyTyped(e)
      updateCaret()
    }
  })

  Pane.addMouseListener(new MouseAdapter() {
    override def mousePressed(e: MouseEvent) {
      controller.processMousePressed(e)
      updateCaret()
      Pane.requestFocusInWindow()
    }
  })

  Pane.addMouseMotionListener(new MouseMotionAdapter() {
    override def mouseDragged(e: MouseEvent) {
      controller.processMouseDragged(e)
      updateCaret()
    }

    override def mouseMoved(e: MouseEvent) {
      controller.processMouseMoved(e)
    }
  })

  Pane.addFocusListener(new FocusListener {
    def focusGained(e: FocusEvent) {
      updateCaret()
    }

    def focusLost(e: FocusEvent) {
      updateCaret()
    }
  })

  private object MyTerminal extends AbstractTerminal {
    def choose[T <: AnyRef](variants: Seq[T], query: String)(callback: T => Unit) {
      val point = toPoint(offset)
      val shifted = new Point(point.x - grid.cellSize.width * query.length - 3, point.y + 20)
      val (popup, list) = ChooserFactory.createPopup(Pane, shifted, NormalFont, variants, listRenderer) { it =>
        Pane.requestFocusInWindow() // to draw cursor immediately
        popupVisible = false
        it.foreach(callback)
      }
      popup.show()
      list.requestFocusInWindow()
      popupVisible = true
    }

    def edit(text: String, title: String)(callback: Option[String] => Unit) {
      val dialog = DialogFactory.create(Pane, text, title) { result =>
        Pane.requestFocusInWindow() // to draw cursor immediately
        callback(result)
      }
      dialog.pack()
      dialog.setLocationRelativeTo(scroll)
      dialog.setVisible(true)
    }
  }

  private object Pane extends JComponent with Scrollable {
    setOpaque(true)
    setBorder(new EmptyBorder(10, 5, 10, 5))
    setFocusable(true)
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR))
    setFocusTraversalKeysEnabled(false)

    def getPreferredScrollableViewportSize = getPreferredSize

    def getScrollableUnitIncrement(visibleRect: Rectangle, orientation: Int, direction: Int) = orientation match {
      case SwingConstants.VERTICAL => grid.cellSize.height
      case SwingConstants.HORIZONTAL => grid.cellSize.width
    }

    def getScrollableBlockIncrement(visibleRect: Rectangle, orientation: Int, direction: Int) = orientation match {
      case SwingConstants.VERTICAL => visibleRect.height
      case SwingConstants.HORIZONTAL => visibleRect.width
    }

    def getScrollableTracksViewportWidth = getParent match {
      case vp: JViewport if vp.getWidth > getPreferredSize.width => true
      case _ => false
    }

    def getScrollableTracksViewportHeight = getParent match {
      case vp: JViewport if vp.getHeight > getPreferredSize.height => true
      case _ => false
    }

    override def paintComponent(g: Graphics) {
      val g2d = g.asInstanceOf[Graphics2D]

      renderingHints.foreach(it => g2d.addRenderingHints(it.asInstanceOf[java.util.Map[_, _]]))

      painters.filter(_.immediate == immediatePainting).foreach(_.paint(g, g.getClipBounds))
    }
  }
}