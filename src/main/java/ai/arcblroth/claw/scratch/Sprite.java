package ai.arcblroth.claw.scratch;

import ai.arcblroth.claw.util.ArrayListQueue;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
public record Sprite(
        // From Target
        int currentCostume,
        Map<String, Block> blocks,
        Map<String, Object> variables,
        Map<String, Object> lists,
        Map<String, Object> broadcasts,
        Map<String, Object> comments,
        ArrayListQueue<Costume> costumes,
        ArrayListQueue<Object> sounds,
        OptionalDouble volume,

        String name,
        Optional<Boolean> visible,
        OptionalDouble x,
        OptionalDouble y,
        OptionalDouble size,
        OptionalDouble direction,
        Optional<Boolean> draggable,
        RotationStyle rotationStyle,

        @JsonProperty(value = "layerOrder", defaultValue = "1")
        OptionalInt layerOrder
) implements Target {
    public enum RotationStyle {
        @JsonProperty("all around") ALL_AROUND,
        @JsonProperty("don't rotate") DONT_ROTATE,
        @JsonProperty("left-right") LEFT_RIGHT,
    }

    public Sprite {
        // Oddly enough the schema doesn't prevent the name from being "Stage"
        if (name.equals("_stage_")) {
            throw new IllegalArgumentException("Sprite cannot be named \"_stage_\"");
        }
    }

    public final boolean isStage() {
        return false;
    }
}
