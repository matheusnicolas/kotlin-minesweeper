package model

import java.util.Random

enum class EventTable { VICTORY, DEFEAT }

class Table(val totalLines: Int, val totalColumns: Int, private val totalMines: Int) {
    private val fields = ArrayList<ArrayList<Field>>()
    private val callbacks = ArrayList<(EventTable) -> Unit>()

    init {
        generateFields()
        associateNeighbors()
        sortMines()
    }

    private fun generateFields() {
        for (line in 0 until totalLines) {
            fields.add(ArrayList())
            for (column in 0 until totalColumns) {
                val newField = Field(line, column)
                newField.onEvent(this::checkVictoryOrDefeat)
                fields[line].add(newField)
            }
        }
    }

    private fun associateNeighbors() {
        forEachField { associateNeighbors(it) }
    }

    private fun associateNeighbors(field: Field) {
        val (line, column) = field
        val lines = arrayOf(line - 1, line, line + 1)
        val columns = arrayOf(column - 1, column, column + 1)

        lines.forEach { line ->
            columns.forEach { column ->
                val current = fields.getOrNull(line)?.getOrNull(column)
                current?.takeIf { field != it }?.let { field.addNeighbor(it) }
            }
        }
    }

    private fun sortMines() {
        val generator = Random()
        var sortedLine = -1
        var sortedColumn = -1
        var totalCurrentMines = 0

        while (totalCurrentMines < this.totalMines) {
            sortedLine = generator.nextInt(totalLines)
            sortedColumn = generator.nextInt(totalColumns)

            val sortedField = fields[sortedLine][sortedColumn]
            if (sortedField.safe) {
                sortedField.mine()
                totalCurrentMines++
            }
        }
    }

    private fun goalAchieved(): Boolean {
        var playerWon = true
        forEachField { if (!it.goalAchieved) playerWon = false }
        return playerWon
    }

    private fun checkVictoryOrDefeat(field: Field, event: EventField) {
        if (event == EventField.EXPLOSION) {
            callbacks.forEach { it(EventTable.DEFEAT) }
        } else if (goalAchieved()) {
            callbacks.forEach { it(EventTable.VICTORY) }
        }
    }

    fun forEachField(callback: (Field) -> Unit) {
        fields.forEach { line -> line.forEach(callback) }
    }

    fun onEvent(callback: (EventTable) -> Unit) {
        callbacks.add(callback)
    }

    fun restart() {
        forEachField { it.restart() }
        sortMines()
    }
}