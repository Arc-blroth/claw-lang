package ai.arcblroth.claw.scratch

import ai.arcblroth.claw.compiler.exceptions.NoSuchIntrinsicException
import ai.arcblroth.claw.util.Either
import java.lang.invoke.MethodHandles
import java.lang.invoke.WrongMethodTypeException
import java.util.Optional
import java.util.OptionalDouble
import java.util.concurrent.atomic.AtomicLong
import kotlin.annotation.Target
import kotlin.jvm.Throws

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

typealias NumberOrBlock = Either<Number, BlockWithId>
typealias StringOrBlock = Either<String, BlockWithId>
typealias ColorOrBlock = Either<Color, BlockWithId>

class BlockFactory {
    class BlockBuilder(val id: String) {
        private var opcode: String? = null
        private var topLevel = false
        private val inputs: LinkedHashMap<String, Input> = linkedMapOf()

        fun opcode(id: String) {
            this.opcode = id
        }

        fun topLevel(topLevel: Boolean) {
            this.topLevel = topLevel
        }

        private fun addInput(name: String, primitive: InputPrimitive) = inputs.put(name, Input(Input.Shadow.UNOBSCURED, primitive))

        fun inputValue(name: String, block: BlockWithId) = addInput(name, BlockInputPrimitive(block.id))

        fun numValue(name: String, num: NumberOrBlock) = addInput(
            name,
            when (num) {
                is Either.Right -> BlockInputPrimitive(num.value.id)
                is Either.Left -> NumberPrimitive(NumberPrimitive.Type.MATH, num.value)
            }
        )

        fun positiveNumValue(name: String, num: NumberOrBlock) = addInput(
            name,
            when (num) {
                is Either.Right -> BlockInputPrimitive(num.value.id)
                is Either.Left -> NumberPrimitive(NumberPrimitive.Type.POSITIVE, num.value)
            }
        )

        fun wholeNumValue(name: String, num: NumberOrBlock) = addInput(
            name,
            when (num) {
                is Either.Right -> BlockInputPrimitive(num.value.id)
                is Either.Left -> NumberPrimitive(NumberPrimitive.Type.WHOLE, num.value)
            }
        )

        fun intNumValue(name: String, num: NumberOrBlock) = addInput(
            name,
            when (num) {
                is Either.Right -> BlockInputPrimitive(num.value.id)
                is Either.Left -> NumberPrimitive(NumberPrimitive.Type.INTEGER, num.value)
            }
        )

        fun angleNumValue(name: String, num: NumberOrBlock) = addInput(
            name,
            when (num) {
                is Either.Right -> BlockInputPrimitive(num.value.id)
                is Either.Left -> NumberPrimitive(NumberPrimitive.Type.ANGLE, num.value)
            }
        )

        fun colorValue(name: String, color: ColorOrBlock) = addInput(
            name,
            when (color) {
                is Either.Right -> BlockInputPrimitive(color.value.id)
                is Either.Left -> ColorPrimitive(color.value)
            }
        )

        fun textValue(name: String, text: StringOrBlock) = addInput(
            name,
            when (text) {
                is Either.Right -> BlockInputPrimitive(text.value.id)
                is Either.Left -> TextPrimitive(text.value)
            }
        )

        fun build() = BlockWithId(
            id,
            requireNotNull(opcode) { "Block opcode must be defined!" },
            topLevel,
            Optional.empty(),
            inputs,
            linkedMapOf(),
            linkedMapOf(),
            Optional.empty(),
            Optional.empty(),
        )
    }

    /**
     * Annotates one of the factory methods below as a statement block
     * builder, or a builder that does not produce a reporter block
     * or hat block.
     */
    @Target(AnnotationTarget.FUNCTION)
    annotation class StatementBlockBuilder

    private val statementBuilders = BlockFactory::class.java.methods
        .filter { it.isAnnotationPresent(StatementBlockBuilder::class.java) && it.returnType == BlockWithId::class.java }
        .associate { Pair(it.name, it.parameterCount) to MethodHandles.insertArguments(MethodHandles.lookup().unreflect(it), 0, this) }

    // Scratch doesn't put any constraints on block ids
    // so we just use a monotonically increasing counter
    private val opcodeCounter = AtomicLong(0)

    private fun nextId() = opcodeCounter.incrementAndGet().toString()

    private fun block(init: BlockBuilder.() -> Unit): BlockWithId {
        val builder = BlockBuilder(nextId())
        builder.init()
        return builder.build()
    }

    @Throws(NoSuchIntrinsicException::class)
    fun buildStatementBlock(name: String, vararg args: Any): BlockWithId {
        try {
            val actualArgs = Array(args.size) { i ->
                when (val arg = args[i]) {
                    is BlockWithId -> Either.Right(arg)
                    is Number -> Either.Left(arg)
                    is String -> Either.Left(arg)
                    is Color -> Either.Left(arg)
                    else -> throw WrongMethodTypeException("Unknown statement block input type")
                }
            }
            return statementBuilders.getOrElse(Pair(name, args.size)) {
                val argsList = args.joinToString(", ", "(", ")") { it.javaClass.name }
                throw NoSuchIntrinsicException(name + argsList)
            }.invokeWithArguments(*actualArgs) as BlockWithId
        } catch (e: WrongMethodTypeException) {
            val argsList = args.joinToString(", ", "(", ")") { it.javaClass.name }
            throw NoSuchIntrinsicException(name + argsList)
        }
    }

    // ====================================================
    //                   Motion Blocks
    // ====================================================
    @StatementBlockBuilder
    fun moveSteps(steps: NumberOrBlock) = block {
        opcode("motion_movesteps")
        numValue("STEPS", steps)
    }

    @StatementBlockBuilder
    fun turnRight(degrees: NumberOrBlock) = block {
        opcode("motion_turnright")
        numValue("DEGREES", degrees)
    }

    @StatementBlockBuilder
    fun turnLeft(degrees: NumberOrBlock) = block {
        opcode("motion_turnleft")
        numValue("DEGREES", degrees)
    }

    @StatementBlockBuilder
    fun pointInDirection(angle: NumberOrBlock) = block {
        opcode("motion_pointindirection")
        angleNumValue("DIRECTION", angle)
    }

    @StatementBlockBuilder
    fun goTo(x: NumberOrBlock, y: NumberOrBlock) = block {
        opcode("motion_gotoxy")
        numValue("X", x)
        numValue("Y", y)
    }

    @StatementBlockBuilder
    fun glide(secs: NumberOrBlock, x: NumberOrBlock, y: NumberOrBlock) = block {
        opcode("motion_glidesecstoxy")
        numValue("SECS", secs)
        numValue("X", x)
        numValue("Y", y)
    }

    @StatementBlockBuilder
    fun changeX(dx: NumberOrBlock) = block {
        opcode("motion_changexby")
        numValue("DX", dx)
    }

    @StatementBlockBuilder
    fun setX(x: NumberOrBlock) = block {
        opcode("motion_setx")
        numValue("X", x)
    }

    @StatementBlockBuilder
    fun changeY(dy: NumberOrBlock) = block {
        opcode("motion_changeyby")
        numValue("DY", dy)
    }

    @StatementBlockBuilder
    fun setY(y: NumberOrBlock) = block {
        opcode("motion_sety")
        numValue("Y", y)
    }

    @StatementBlockBuilder
    fun ifOnEdgeBounce() = block {
        opcode("motion_ifonedgebounce")
    }

    // ====================================================
    //                     Look Blocks
    // ====================================================
    @StatementBlockBuilder
    fun say(message: StringOrBlock, secs: NumberOrBlock) = block {
        opcode("looks_sayforsecs")
        textValue("MESSAGE", message)
        numValue("SECS", secs)
    }

    @StatementBlockBuilder
    fun say(message: StringOrBlock) = block {
        opcode("looks_say")
        textValue("MESSAGE", message)
    }

    @StatementBlockBuilder
    fun think(message: StringOrBlock, secs: NumberOrBlock) = block {
        opcode("looks_thinkforsecs")
        textValue("MESSAGE", message)
        numValue("SECS", secs)
    }

    @StatementBlockBuilder
    fun think(message: StringOrBlock) = block {
        opcode("looks_think")
        textValue("MESSAGE", message)
    }

    @StatementBlockBuilder
    fun show() = block {
        opcode("looks_show")
    }

    @StatementBlockBuilder
    fun hide() = block {
        opcode("looks_hide")
    }

    @StatementBlockBuilder
    fun clearGraphicEffects() = block {
        opcode("looks_cleargraphiceffects")
    }

    @StatementBlockBuilder
    fun changeSize(amount: NumberOrBlock) = block {
        opcode("looks_changesizeby")
        numValue("CHANGE", amount)
    }

    @StatementBlockBuilder
    fun setSize(size: NumberOrBlock) = block {
        opcode("looks_setsizeto")
        numValue("SIZE", size)
    }

    @StatementBlockBuilder
    fun nextCostume() = block {
        opcode("looks_nextcostume")
    }

    @StatementBlockBuilder
    fun nextBackdrop() = block {
        opcode("looks_nextbackdrop")
    }

    // TODO: tag this block with the undefined() comment
    @StatementBlockBuilder
    fun undefined() = block {
        opcode("looks_hideallsprites")
    }

    // ====================================================
    //                    Sound Blocks
    // ====================================================
    @StatementBlockBuilder
    fun stopAllSounds() = block {
        opcode("sound_stopallsounds")
    }

    @StatementBlockBuilder
    fun clearSoundEffects() = block {
        opcode("sound_cleareffects")
    }

    @StatementBlockBuilder
    fun changeVolume(amount: NumberOrBlock) = block {
        opcode("sound_changevolumeby")
        numValue("VOLUME", amount)
    }

    @StatementBlockBuilder
    fun setVolume(volume: NumberOrBlock) = block {
        opcode("sound_setvolumeto")
        numValue("VOLUME", volume)
    }

    // ====================================================
    //                    Event Blocks
    // ====================================================
    fun onGreenFlag() = block {
        opcode("event_whenflagclicked")
        topLevel(true)
    }

    // ====================================================
    //                   Control Blocks
    // ====================================================
    @StatementBlockBuilder
    fun wait(secs: NumberOrBlock) = block {
        opcode("control_wait")
        numValue("DURATION", secs)
    }

    // ====================================================
    //                   Sensing Blocks
    // ====================================================
    @StatementBlockBuilder
    fun askAndWait(question: StringOrBlock) = block {
        opcode("sensing_askandwait")
        textValue("QUESTION", question)
    }

    @StatementBlockBuilder
    fun resetTimer() = block {
        opcode("sensing_resettimer")
    }
}
