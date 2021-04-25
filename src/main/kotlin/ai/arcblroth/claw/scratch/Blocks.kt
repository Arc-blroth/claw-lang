package ai.arcblroth.claw.scratch

import java.util.Optional
import java.util.OptionalDouble
import java.util.concurrent.atomic.AtomicLong

data class BlockWithId(
    val id: String,
    val opcode: String,
    val topLevel: Boolean,
    var comment: Optional<String>,
    var inputs: LinkedHashMap<String, Input>,
    var fields: LinkedHashMap<String, Any>,
    var mutation: LinkedHashMap<String, Any>,
    var parent: Optional<BlockWithId>,
    var next: Optional<BlockWithId>,
) {
    fun toBlock(): Block = Block(
        opcode = opcode,
        topLevel = topLevel,
        comment = comment,
        inputs = inputs,
        fields = fields,
        mutation = mutation,
        parent = parent.map { it.id },
        next = next.map { it.id },
        x = OptionalDouble.empty(),
        y = OptionalDouble.empty(),
        shadow = false,
    )
}

fun MutableMap<String, Block>.pushBlock(block: BlockWithId) {
    this[block.id] = block.toBlock()
}

class BlockFactory {
    class BlockBuilder(val id: String) {
        private var opcode: String? = null
        private var topLevel = false
        private val inputs: LinkedHashMap<String, InputPrimitive> = linkedMapOf()

        fun opcode(id: String) {
            this.opcode = id
        }

        fun topLevel(topLevel: Boolean) {
            this.topLevel = topLevel
        }

        fun inputValue(name: String, block: BlockWithId) = inputs.put(name, BlockInputPrimitive(block.id))

        fun build() = BlockWithId(
            id,
            requireNotNull(opcode) { "Block opcode must be defined!" },
            topLevel,
            Optional.empty(),
            linkedMapOf(),
            linkedMapOf(),
            linkedMapOf(),
            Optional.empty(),
            Optional.empty(),
        )
    }

    // Scratch doesn't put any constraints on block ids
    // so we just use a monotonically increasing counter
    private val opcodeCounter = AtomicLong(0)

    private fun nextId() = opcodeCounter.incrementAndGet().toString()

    private fun block(init: BlockBuilder.() -> Unit): BlockWithId {
        val builder = BlockBuilder(nextId())
        builder.init()
        return builder.build()
    }

    fun onGreenFlag() = block {
        opcode("event_whenflagclicked")
        topLevel(true)
    }
}
