package ai.arcblroth.claw.scratch;

import ai.arcblroth.claw.util.ArrayListQueue;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;
import java.util.OptionalDouble;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public interface Target {
    @JsonProperty(value = "isStage", required = true)
    boolean isStage();

    int currentCostume();

    @JsonProperty(required = true)
    Map<String, Block> blocks();

    @JsonProperty(required = true)
    Map<String, Object> variables();

    Map<String, Object> lists();

    Map<String, Object> broadcasts();

    Map<String, Object> comments();

    @JsonProperty(required = true)
    ArrayListQueue<Costume> costumes();

    @JsonProperty(required = true)
    ArrayListQueue<Object> sounds();

    OptionalDouble volume();
}
