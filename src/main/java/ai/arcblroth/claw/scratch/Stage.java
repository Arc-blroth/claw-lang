package ai.arcblroth.claw.scratch;

import ai.arcblroth.claw.util.ArrayListQueue;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
public record Stage(
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

        OptionalDouble tempo,
        OptionalDouble videoTransparency,
        Optional<VideoState> videoState,

        @JsonProperty(value = "layerOrder", defaultValue = "1")
        OptionalInt layerOrder
) implements Target {

    public enum VideoState {
        @JsonProperty("on") ON,
        @JsonProperty("off") OFF,
        @JsonProperty("on-flipped") ON_FLIPPED
    }

    @JsonProperty(required = true, access = JsonProperty.Access.READ_ONLY)
    public final String name() {
        return "Stage";
    }

    public final boolean isStage() {
        return true;
    }
}
