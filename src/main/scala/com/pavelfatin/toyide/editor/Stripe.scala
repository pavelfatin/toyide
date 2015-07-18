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

import com.pavelfatin.toyide.document.{Document, Location}
import javax.swing.{ToolTipManager, JComponent}
import java.awt._
import event._
import com.pavelfatin.toyide.{ObservableEvents, Interval}

private class Stripe(document: Document, data: Data, holder: ErrorHolder, grid: Grid, canvas: Canvas) extends JComponent with ObservableEvents[Int] {
  private val MarkSize = new Dimension(14, 4)
  private val DefaultDelay = ToolTipManager.sharedInstance().getInitialDelay
  private val Led = new Rectangle(2, 2, MarkSize.width - 4, MarkSize.width - 4)

  private var status: Status = Status.Normal
  private var descriptors = Seq.empty[Descriptor]

  setMinimumSize(new Dimension(MarkSize.width, 0))
  setMaximumSize(new Dimension(MarkSize.width, Int.MaxValue))
  setPreferredSize(new Dimension(MarkSize.width, 0))

  canvas.onChange {
    case VisibilityChanged(true) => update()
    case _ =>
  }

  holder.onChange { _ =>
    if (canvas.visible) {
      update()
    }
  }

  data.onChange {
    case DataEvent(Pass.Text, _) if canvas.visible => update()
    case DataEvent(Pass.Inspections, _) if canvas.visible => updateStatus()
    case _ =>
  }

  addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) {
      updateDescriptors()
    }
  })

  addMouseListener(new MouseAdapter() {
    override def mousePressed(e: MouseEvent) {
      notifyObservers(gridY(toLine(e.getPoint.y)))
    }

    override def mouseEntered(e: MouseEvent) {
      ToolTipManager.sharedInstance().setInitialDelay(200)
    }

    override def mouseExited(e: MouseEvent) {
      ToolTipManager.sharedInstance().setInitialDelay(DefaultDelay)
    }
  })

  addMouseMotionListener(new MouseMotionAdapter() {
    override def mouseMoved(e: MouseEvent) {
      if(Led.contains(e.getPoint)) {
        val message = status match {
          case Status.Waiting => "Analyzing..."
          case Status.Normal => "No errors"
          case Status.Warnings => "%d warnings(s) found".format(warnings.size)
          case Status.Errors =>
            if (warnings.isEmpty)
              "%d error(s) found".format(errors.size)
            else
              "%d error(s), %d warning(s) found".format(errors.size, warnings.size)
        }
        setToolTipText(message)
      } else {
        val pointDescriptors = descriptors.filter(_.rectangle.contains(e.getPoint))
        val descriptor = pointDescriptors.sortBy(!_.error.fatal).headOption
        descriptor.foreach { it =>
          setToolTipText(it.error.message)
          setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
        }
        if(descriptor.isEmpty) {
          setToolTipText(null)
          setCursor(Cursor.getDefaultCursor)
        }
      }
    }
  })

  private def errors = descriptors.filter(_.error.fatal)

  private def warnings = descriptors.filter(!_.error.fatal)

  private def update() {
    updateStatus()
    updateDescriptors()
  }

  private def updateStatus() {
    val newStatus = statusIn(data)

    if (status != newStatus) {
      status = newStatus
      repaint(Led)
    }
  }

  private def updateDescriptors() {
    val newDescriptors = descriptorsIn(holder)

    if (descriptors != newDescriptors) {
      descriptors = newDescriptors
      repaint()
    }
  }

  private def gridY(line: Int) = grid.toPoint(Location(line, 0)).y

  private def toY(line: Int) = {
    if(gridY(document.linesCount) < getHeight) gridY(line) + 3 else
      math.round(getHeight.toDouble * line / document.linesCount).toInt
  }

  private def toLine(y: Int) = {
    (0 until document.linesCount).map(y => (y, toY(y))).takeWhile(_._2 < y).last._1
  }

  private def lineHeight = toY(1) - toY(0)

  override def paintComponent(g: Graphics) {
    paint(g, status)
    paint(g, descriptors)
  }

  private def paint(g: Graphics, status: Status) {
    val led = status match {
      case Status.Waiting => Color.LIGHT_GRAY
      case Status.Normal => Color.GREEN
      case Status.Warnings => Color.YELLOW
      case Status.Errors => Color.RED
    }

    g.setColor(led)
    g.fill3DRect(Led.x, Led.y, Led.width, Led.height, false)
  }

  private def paint(g: Graphics, descriptors: Seq[Descriptor]) {
    descriptors.sortBy(_.error.fatal).foreach { it =>
      val color = if (it.error.fatal) Color.RED else Color.YELLOW
      g.setColor(color)
      val r = it.rectangle
      g.fill3DRect(r.x, r.y, r.width, r.height, true)
    }
  }

  private def statusIn(data: Data): Status = {
    val errors = data.errors

    data.hasNextPass match {
      case true => Status.Waiting
      case false =>
        if(errors.isEmpty) Status.Normal
        else if (data.hasFatalErrors) Status.Errors else Status.Warnings
    }
  }

  private def descriptorsIn(holder: ErrorHolder): Seq[Descriptor] = {
    val errors = holder.errors

    val descriptors = errors.map { error =>
      val lines = error.interval.transformWith(document.lineNumberOf)
      val heights = lines.transformWith(toY)

      val offset = math.round((lineHeight.toDouble - MarkSize.height) / 2.0D).toInt

      val (y, height) = if(heights.empty)
        (heights.begin + offset, MarkSize.height)
      else
        (heights.begin, heights.length)

      Descriptor(error, lines.withEndShift(1), new Rectangle(2, y, MarkSize.width - 4, height))
    }

    descriptors
  }

  private case class Descriptor(error: Error, lines: Interval, rectangle: Rectangle)

  private sealed trait Status

  private object Status {
    case object Waiting extends Status

    case object Normal extends Status

    case object Warnings extends Status

    case object Errors extends Status
  }
}