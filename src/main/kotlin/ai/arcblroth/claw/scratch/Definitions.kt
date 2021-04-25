package ai.arcblroth.claw.scratch

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import java.util.Optional
import java.util.OptionalDouble
import java.util.OptionalInt
import kotlin.jvm.Throws

typealias OptionalBoolean = Optional<Boolean>

data class AssetId(@get:JsonValue val id: String) {
    init {
        require(id.matches(Regex("^[a-fA-F0-9]{32}$"))) {
            "Invalid asset id"
        }
    }
}

data class MD5Ext(@get:JsonValue val id: String) {
    init {
        require(id.matches(Regex("^[a-fA-F0-9]{32}\\.[a-zA-Z]+\$"))) {
            "Invalid md5ext"
        }
    }
}

/**
 * A Scratch 3.0 project.
 */
data class Project(val meta: Meta, val targets: ArrayList<Target>) {
    data class Meta(val semver: String, val vm: String, val agent: String)
}

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
sealed class Target {
    @JsonProperty(value = "isStage", required = true)
    abstract fun isStage(): Boolean

    abstract val currentCostume: Int

    @get:JsonProperty(required = true)
    abstract val blocks: MutableMap<String, Block>

    @get:JsonProperty(required = true)
    abstract val variables: MutableMap<String, Any>

    abstract val lists: MutableMap<String, Any>
    abstract val broadcasts: MutableMap<String, Any>
    abstract val comments: MutableMap<String, Any>

    @get:JsonProperty(required = true)
    abstract val costumes: ArrayList<Costume>

    @get:JsonProperty(required = true)
    abstract val sounds: ArrayList<Any>

    abstract val volume: OptionalDouble
}

@JsonInclude(JsonInclude.Include.NON_ABSENT)
data class Sprite(
    // From Target
    override val currentCostume: Int,
    override val blocks: MutableMap<String, Block>,
    override val variables: MutableMap<String, Any>,
    override val lists: MutableMap<String, Any>,
    override val broadcasts: MutableMap<String, Any>,
    override val comments: MutableMap<String, Any>,
    override val costumes: ArrayList<Costume>,
    override val sounds: ArrayList<Any>,
    override val volume: OptionalDouble,

    val name: String,
    val visible: OptionalBoolean,
    val x: OptionalDouble,
    val y: OptionalDouble,
    val size: OptionalDouble,
    val direction: OptionalDouble,
    val draggable: OptionalBoolean,
    val rotationStyle: RotationStyle,

    @JsonProperty(value = "layerOrder", defaultValue = "1")
    val layerOrder: OptionalInt,
) : Target() {
    enum class RotationStyle {
        @JsonProperty("all around")
        ALL_AROUND,
        @JsonProperty("don't rotate")
        DONT_ROTATE,
        @JsonProperty("left-right")
        LEFT_RIGHT,
    }

    init {
        // Oddly enough the schema doesn't prevent the name from being "Stage"
        if (name == "_stage_") {
            throw IllegalArgumentException("Sprite cannot be named \"_stage_\"")
        }
    }

    override fun isStage(): Boolean {
        return false
    }
}

@JsonInclude(JsonInclude.Include.NON_ABSENT)
data class Stage(
    // From Target
    override val currentCostume: Int,
    override val blocks: MutableMap<String, Block>,
    override val variables: MutableMap<String, Any>,
    override val lists: MutableMap<String, Any>,
    override val broadcasts: MutableMap<String, Any>,
    override val comments: MutableMap<String, Any>,
    override val costumes: ArrayList<Costume>,
    override val sounds: ArrayList<Any>,
    override val volume: OptionalDouble,

    val tempo: OptionalDouble,
    val videoTransparency: OptionalDouble,
    val videoState: Optional<VideoState>,

    @JsonProperty(value = "layerOrder", defaultValue = "1")
    val layerOrder: OptionalInt
) : Target() {
    enum class VideoState {
        @JsonProperty("on")
        ON,
        @JsonProperty("off")
        OFF,
        @JsonProperty("on-flipped")
        ON_FLIPPED
    }

    @JsonProperty(required = true, access = JsonProperty.Access.READ_ONLY)
    fun getName(): String {
        return "Stage"
    }

    override fun isStage(): Boolean {
        return true
    }
}

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Block(
    @JsonProperty(value = "opcode", required = true)
    val opcode: String,

    val comment: Optional<String>,
    val inputs: Map<String, Input>,
    val fields: Map<String, Any>,
    val next: Optional<String>,
    val topLevel: Boolean,
    val parent: Optional<String>,
    val shadow: Boolean,
    val x: OptionalDouble,
    val y: OptionalDouble,
    val mutation: Map<String, Any>,
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Costume(
    @JsonProperty(value = "assetId", required = true)
    val assetId: AssetId,

    val bitmapResolution: OptionalInt,

    @JsonProperty(value = "dataFormat", required = true)
    val dataFormat: DataFormat,

    val md5ext: MD5Ext,

    @JsonProperty(value = "name", required = true)
    val name: String,

    val rotationCenterX: Int,
    val rotationCenterY: Int,
) {
    companion object {
        val EMPTY_COSTUME = Costume(
            AssetId("cd21514d0531fdffb22204e0ec5ed84a"),
            OptionalInt.empty(),
            DataFormat.SVG,
            MD5Ext("cd21514d0531fdffb22204e0ec5ed84a.svg"),
            "Default Costume",
            240,
            180,
        )
    }

    enum class DataFormat {
        @JsonProperty("png")
        PNG,
        @JsonProperty("svg")
        SVG,
        @JsonProperty("jpeg")
        JPEG,
        @JsonProperty("jpg")
        JPG,
        @JsonProperty("bmp")
        BMP,
        @JsonProperty("gif")
        GIF,
    }
}

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class Input(val shadow: Shadow, val inputs: List<InputPrimitive>) {
    enum class Shadow {
        UNOBSCURED,
        NONE,
        OBSCURED;

        @JsonValue
        fun asInt() = ordinal + 1
    }
}

// Input Primitive Types:
// https://github.com/LLK/scratch-vm/blob/develop/src/serialization/sb3.js#L63

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
sealed class InputPrimitive

/**
 * While this is not actually a Scratch input primitive,
 * this class exists so inheritance makes sense for [Input.inputs]
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
data class BlockInputPrimitive(@JsonValue val blockId: String) : InputPrimitive()

data class NumberPrimitive(val type: Type, val number: Number) : InputPrimitive() {
    enum class Type {
        MATH,
        POSITIVE,
        WHOLE,
        INTEGER,
        ANGLE;

        @JsonValue
        fun asInt() = ordinal + 4
    }
}

@JsonSerialize(using = Color.Serializer::class)
@JsonDeserialize(using = Color.Deserializer::class)
data class Color(val rgb: Int) {
    constructor(r: Int, g: Int, b: Int) : this(((r and 0xFF) shl 16) or ((g and 0xFF) shl 8) or ((b and 0xFF)))

    val red: Int
        get() = (rgb shr 16) and 0xFF

    val green: Int
        get() = (rgb shr 8) and 0xFF

    val blue: Int
        get() = rgb and 0xFF

    class Serializer : JsonSerializer<Color>() {
        override fun serialize(value: Color, gen: JsonGenerator, serializers: SerializerProvider) {
            gen.writeString("#" + value.rgb.toString(16))
        }
    }

    class Deserializer : JsonDeserializer<Color>() {
        @Throws(JsonProcessingException::class)
        override fun deserialize(p: JsonParser, ctx: DeserializationContext): Color {
            val string = p.valueAsString
            if (string.matches(Regex("^#[a-fA-F0-9]{6}\$"))) {
                return Color(string.substring(1).toInt())
            } else {
                throw InvalidFormatException(p, "Invalid color value", string, Color::class.java)
            }
        }
    }
}

@JsonPropertyOrder("type", "color")
data class ColorPrimitive(val color: Color) : InputPrimitive() {
    @JsonProperty("type")
    fun getType(): Int = 9
}

@JsonPropertyOrder("type", "text")
data class TextPrimitive(val text: String) : InputPrimitive() {
    @JsonProperty("type")
    fun getType(): Int = 10
}

@JsonPropertyOrder("type", "message", "messageId")
data class BroadcastPrimitive(val message: String, val messageId: String) : InputPrimitive() {
    @JsonProperty("type")
    fun getType(): Int = 11
}

@JsonPropertyOrder("type", "name", "id")
data class VariablePrimitive(val name: String, val id: String) : InputPrimitive() {
    @JsonProperty("type")
    fun getType(): Int = 12
}

@JsonPropertyOrder("type", "name", "id")
data class ListPrimitive(val name: String, val id: String) : InputPrimitive() {
    @JsonProperty("type")
    fun getType(): Int = 13
}
