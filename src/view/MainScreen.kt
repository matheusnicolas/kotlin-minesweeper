package view

import model.EventTable
import model.Table
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

class MainScreen : JFrame() {
    private val table = Table(totalLines = 16, totalColumns = 30, totalMines = 89)
    private val tablePanel = TablePanel(table)

    init {
        table.onEvent(this::showResult)
        add(tablePanel)

        setSize(690, 438)
        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
        title = "Kotlin Minesweeper"
        isVisible = true
    }

    private fun showResult(event: EventTable) {
        SwingUtilities.invokeLater {
            val message = when(event) {
                EventTable.VICTORY -> "YOU WIN!"
                EventTable.DEFEAT -> "YOU LOOSE!"
            }

            JOptionPane.showMessageDialog(null, message)
            table.restart()
            tablePanel.repaint()
            tablePanel.validate()
        }
    }
}