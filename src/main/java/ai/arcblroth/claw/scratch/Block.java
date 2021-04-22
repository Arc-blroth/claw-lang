package ai.arcblroth.claw.scratch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Block(
        @JsonProperty(value = "opcode", required = true)
        String opcode,

        String comment,
        Map<String, Object> inputs,
        Map<String, Object> fields,
        String next,
        boolean topLevel,
        String parent,
        boolean shadow,
        double x,
        double y,
        Map<String, Object> mutation
) {

}
