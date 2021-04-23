package ai.arcblroth.claw.scratch

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import java.util.Optional
import java.util.OptionalDouble
import java.util.OptionalInt

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
    abstract val blocks: Map<String, Block>

    @get:JsonProperty(required = true)
    abstract val variables: Map<String, Any>

    abstract val lists: Map<String, Any>
    abstract val broadcasts: Map<String, Any>
    abstract val comments: Map<String, Any>

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
    override val blocks: Map<String, Block>,
    override val variables: Map<String, Any>,
    override val lists: Map<String, Any>,
    override val broadcasts: Map<String, Any>,
    override val comments: Map<String, Any>,
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
    override val blocks: Map<String, Block>,
    override val variables: Map<String, Any>,
    override val lists: Map<String, Any>,
    override val broadcasts: Map<String, Any>,
    override val comments: Map<String, Any>,
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

    val comment: String,
    val inputs: Map<String, Any>,
    val fields: Map<String, Any>,
    val next: String,
    val topLevel: Boolean,
    val parent: String,
    val shadow: Boolean,
    val x: Double,
    val y: Double,
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
