package com.willfp.libreforge.counters.bind

import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.counters.Accumulator
import com.willfp.libreforge.counters.Counter
import net.kyori.adventure.chat.ChatType.Bound

internal object BoundCounters {
    private val bindings = listMap<Counter, BoundCounter>()

    fun bind(counter: Counter, accumulator: Accumulator) {
        bindings[counter].add(BoundCounter(counter, accumulator))
    }

    fun unbind(counter: Counter) {
        bindings.remove(counter)
    }

    fun values(): Set<Counter> =
        bindings.keys

    val Counter.bindings: List<BoundCounter>
        get() = BoundCounters.bindings[this]
}
