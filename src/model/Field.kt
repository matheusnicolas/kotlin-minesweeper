package model

enum class EventField { OPENING, CHECKED, UNCHECKED, EXPLOSION, REINITIALIZATION }

data class Field(val line: Int, val column: Int) {
    private val neighbors = ArrayList<Field>();
    private val callbacks = ArrayList<(Field, EventField) -> Unit>()

    var checked: Boolean = false
    var open: Boolean = false
    var mined: Boolean = false

    // read-only
    val unchecked: Boolean get() = !checked
    val closed: Boolean get() = !open
    val safe: Boolean get() = !mined
    val goalAchieved: Boolean get() = safe && open || mined && checked
    val totalMinedNeighbors: Int get() = neighbors.filter { it.mined }.size
    val safeNeighbors: Boolean get() = neighbors.map { it.safe }.reduce { result, safe -> result && safe }

    fun addNeighbor(neighbor: Field) {
        neighbors.add(neighbor)
    }

    fun onEvent(callback: (Field, EventField) -> Unit) {
        callbacks.add(callback)
    }

    fun open() {
        if (closed) {
            open = true

            if (mined) {
                callbacks.forEach { it(this, EventField.EXPLOSION) }
            } else {
                callbacks.forEach { it(this, EventField.OPENING) }
                neighbors.filter { it.closed && it.safe && safeNeighbors }.forEach { it.open() }
            }
        }
    }

    fun updateMarker() {
        if (closed) {
            checked = !checked
            val event = if (checked) EventField.CHECKED else EventField.UNCHECKED
            callbacks.forEach { it(this, event) }
        }
    }

    fun mine() {
        mined = true
    }

    fun restart() {
        open = false
        mined = false
        checked = false
        callbacks.forEach { it(this, EventField.REINITIALIZATION) }
    }
}