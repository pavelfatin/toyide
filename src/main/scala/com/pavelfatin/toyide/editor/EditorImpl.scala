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

import controller.{ControllerImpl, Controller}
import javax.swing.border.EmptyBorder
import java.awt.{Point => _, _}
import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.Interval
import javax.swing.{Renderer => _, _}
import com.pavelfatin.toyide.node.Node
import com.pavelfatin.toyide.inspection.Decoration
import java.awt.event._
import com.pavelfatin.toyide.formatter.{FormatterImpl, Format}

private class EditorImpl(val document: Document, val data: Data, coloring: Coloring, matcher: BraceMatcher, format: Format,
                         adviser: Adviser, listRenderer: ListCellRenderer, comment: String, history: History) extends Editor {
  private val CurrentLineColor = new Color(255, 255, 215)

  private val FillColor = new Color(246, 235, 188)

  private val SelectionColor = new Color(82, 109, 165)

  private val HighlightColor = new Color(224, 240, 255)

  private val grid = new Grid(Size(8, 20), Pane.getInsets)

  private val NormalFont = new Font("Monospaced", Font.PLAIN, 14)

  private val Accent = 15

  private val Decent = 4

  private val TabSize = 2

  private lazy val renderingHints = Option(Toolkit.getDefaultToolkit.getDesktopProperty("awt.font.desktophints"))

  private val renderer = new RendererImpl(coloring, matcher)

  private val controller: Controller =
    new ControllerImpl(document, data, terminal, grid, adviser, new FormatterImpl(format), TabSize, comment, history)

  private val scroll = new JScrollPane(Pane)

  val component = {
    val stripe = new Stripe(document, data, grid)
    stripe.onChange { y =>
      val point = toPoint(terminal.offset)
      terminal.offset = document.toNearestOffset(grid.toLocation(Point(point.x, y)))
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
    _message = m
    notifyObservers()
  }

  def dispose() {
    tooltipHandler.dispose()
    timer.stop()
  }

  def terminal: Terminal = MyTerminal

  terminal.onChange { event =>
    event match {
      case CaretMovement(_, offset) =>
        scrollToOffsetVisible(offset)
      case HighlightsChange(_, Seq(interval, etc @ _*)) =>
        scrollToOffsetVisible(interval.begin)
      case _ =>
    }
    Pane.repaint()
  }

  private var cursorVisible = false

  private var popupVisible = false

  protected def structure: Option[Node] = data.structure

  private def toPoint(offset: Int): Point = grid.toPoint(document.toLocation(offset))

  private val tooltipHandler = new TooltipHandler(Pane,
    point => document.toOffset(grid.toLocation(point)).flatMap(errorAt))

  private val timer = new Timer(500, new ActionListener() {
    def actionPerformed(e: ActionEvent) {
      if(data.hasNextPass)
        data.nextPass()

      if(!data.hasNextPass) {
        cursorVisible = !cursorVisible
        Pane.repaint()
      }
    }
  })

  timer.start()

  // handle external changes
  document.onChange { event =>
    terminal.offset = terminal.offset.min(document.length)
    val selection = terminal.selection.map(it => Interval(it.begin.min(document.length), it.end.min(document.length)))
    terminal.selection = selection.filterNot(_.empty)
    terminal.highlights = Seq.empty
  }

  data.onChange {
    update()
  }

  private def scrollToOffsetVisible(offset: Int) {
    val h = grid.cellSize.height
    val w = grid.cellSize.width
    val p = toPoint(offset)

    val spot = {
      val panelBounds = Pane.getBounds(null)
      panelBounds.setLocation(0, 0)
      panelBounds.intersection(new Rectangle(p.x - w * 2, p.y - Accent - h, w * 4, Decent + h * 3))
    }

    if (!scroll.getViewport.getViewRect.contains(spot)) {
      Pane.scrollRectToVisible(spot)
    }
  }

  private def update() {
    message = errorAt(terminal.offset).map(_.message)
    cursorVisible = true
    timer.restart()
    Pane.repaint()
    Pane.revalidate()
  }

  private def errorAt(offset: Int): Option[Error] = {
    val errors = data.errors.filter(_.span.withEndShift(1).includes(offset))
    errors.sortBy(!_.fatal).headOption
  }

  Pane.addKeyListener(new KeyAdapter() {
    override def keyPressed(e: KeyEvent) {
      controller.processKeyPressed(e)
      update()
    }

    override def keyTyped(e: KeyEvent) {
      controller.processKeyTyped(e)
      update()
    }
  })

  Pane.addMouseListener(new MouseAdapter() {
    override def mousePressed(e: MouseEvent) {
      controller.processMousePressed(e)
      update()
      Pane.requestFocusInWindow()
    }
  })

  Pane.addMouseMotionListener(new MouseMotionAdapter() {
    override def mouseDragged(e: MouseEvent) {
      controller.processMouseDragged(e)
      update()
    }

    override def mouseMoved(e: MouseEvent) {
      controller.processMouseMoved(e)
    }
  })

  private object MyTerminal extends AbstractTerminal {
    def choose[T <: AnyRef](variants: Seq[T], query: String)(callback: T => Unit) {
      val point = toPoint(offset)
      val shifted = point.copy(x = point.x - grid.cellSize.width * query.length, y = point.y + Decent)
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
    setBackground(Color.WHITE)
    setBorder(new EmptyBorder(5, 5, 5, 5))
    setFocusable(true)
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR))
    setFocusTraversalKeysEnabled(false)

    addFocusListener(new FocusAdapter {
      override def focusGained(e: FocusEvent) {
        EditorImpl.this.update()
      }
    })

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

    override def getPreferredSize = {
      val size = grid.toSize(document.linesCount, document.maximumIndent)
      new Dimension(size.width, size.height)
    }

    override def paintComponent(g: Graphics) {
      val g2d = g.asInstanceOf[Graphics2D]

      renderingHints.foreach(it => g2d.addRenderingHints(it.asInstanceOf[java.util.Map[_, _]]))

      g2d.setColor(getBackground)
      g2d.fillRect(0, 0, getWidth, getHeight)

      val y = toPoint(terminal.offset).y
      g2d.setColor(CurrentLineColor)
      g2d.fillRect(0, y - Accent, getWidth, grid.cellSize.height)

      data.errors.filter(_.decoration == Decoration.Fill).foreach { mark =>
        fillInterval(g2d, mark.span, FillColor)
      }

      terminal.selection.foreach(fillInterval(g2d, _, SelectionColor))
      terminal.highlights.foreach(fillInterval(g2d, _, HighlightColor))

      val r = scroll.getViewport.getViewRect
      val begin = r.y / grid.cellSize.height
      val end = (r.y + r.height) / grid.cellSize.height

      renderer.render(data, terminal, begin, end).foreach { text =>
        val p = grid.toPoint(text.location)

        text.attributes.background.foreach { color =>
          g2d.setColor(color)
          g2d.fillRect(p.x, p.y - Accent, grid.cellSize.width, grid.cellSize.height)
        }

        g2d.setColor(text.attributes.color)
        g2d.drawString(text.decorated.getIterator, p.x, p.y)
      }

      data.errors.filter(_.decoration == Decoration.Underline).foreach { error =>
        val span = error.span
        val p = toPoint(span.begin)
        g2d.setColor(Color.RED)
        drawWavyLine(g2d, p.x, p.y + 2, 1.max(span.length) * grid.cellSize.width)
      }

      if((isFocusOwner || popupVisible) && cursorVisible) {
        val p = toPoint(terminal.offset)
        g2d.setColor(Color.BLACK)
        g2d.setStroke(new BasicStroke(1.0F))
        g2d.drawLine(p.x + 0, p.y + Decent, p.x + 0, p.y - Accent)
        g2d.drawLine(p.x + 1, p.y + Decent, p.x + 1, p.y - Accent)
      }
    }

    private def fillInterval(g: Graphics2D, interval: Interval, color: Color) {
      val begin = document.toLocation(interval.begin)
      val end = document.toLocation(interval.end)
      val a = grid.toPoint(begin)
      val b = grid.toPoint(end)
      g.setColor(color)
      val y1 = a.y - Accent
      if(begin.line == end.line) {
        g.fillRect(a.x, y1, b.x - a.x, grid.cellSize.height)
      } else {
        g.fillRect(a.x, y1, getWidth - a.x, grid.cellSize.height)
        g.fillRect(5, y1 + grid.cellSize.height, getWidth - 5, b.y - a.y - grid.cellSize.height)
        g.fillRect(5, b.y - Accent, b.x - 5, grid.cellSize.height)
      }
    }

    private def drawWavyLine(g: Graphics, x: Int, y: Int, length: Int) {
      val xs = Range(x, x + length, 2)
      val points = xs.size
      val ys = Stream.continually(()).flatMap(it => Seq(y + 1, y - 1)).take(points)
      g.drawPolyline(xs.toArray, ys.toArray, points)
    }
  }
}