package view

import model.EventField
import model.Field
import java.awt.Color
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.SwingUtilities

private val NORMAL_BACKGROUND_COLOR = Color(184, 184, 184)
private val CHECKED_BACKGROUND_COLOR = Color(8, 179, 247)
private val EXPLOSION_BACKGROUND_COLOR = Color(189, 66, 68)
private val GREEN_TEXT_COLOR = Color(0, 100, 0)

class FieldButton(private val field: Field) : JButton() {

    init {
        font = font.deriveFont(Font.BOLD)
        background = NORMAL_BACKGROUND_COLOR
        isOpaque = true
        border = BorderFactory.createBevelBorder(0)
        addMouseListener(MouseClickListener(field, { it.open() }, { it.updateMarker() }))

        field.onEvent(this::applyStyle)
    }

    private fun applyStyle(field: Field, event: EventField) {
        when (event) {
            EventField.EXPLOSION -> applyExplodedStyle()
            EventField.OPENING -> applyOpenStyle()
            EventField.CHECKED -> applyCheckedStyle()
            else -> applyDefaultStyle()
        }

        SwingUtilities.invokeLater {
            repaint()
            validate()
        }
    }

    private fun applyExplodedStyle() {
        background = EXPLOSION_BACKGROUND_COLOR
        text = "X"
    }

    private fun applyOpenStyle() {
        background = NORMAL_BACKGROUND_COLOR
        border = BorderFactory.createLineBorder(Color.GRAY)

        foreground = when (field.totalMinedNeighbors) {
            1 -> GREEN_TEXT_COLOR
            2 -> Color.BLUE
            3 -> Color.YELLOW
            4, 5, 6 -> Color.RED
            else -> Color.PINK
        }

        text = if (field.totalMinedNeighbors > 0) field.totalMinedNeighbors.toString() else ""
    }

    private fun applyCheckedStyle() {
        background = CHECKED_BACKGROUND_COLOR
        foreground = Color.BLACK
        text = "M"
    }

    private fun applyDefaultStyle() {
        background = NORMAL_BACKGROUND_COLOR
        border = BorderFactory.createBevelBorder(0)
        text = ""
    }
}