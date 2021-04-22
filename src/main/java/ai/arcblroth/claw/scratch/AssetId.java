package ai.arcblroth.claw.scratch;

import com.fasterxml.jackson.annotation.JsonValue;

public record AssetId(String id) {
    public AssetId {
        if (!id.matches("^[a-fA-F0-9]{32}$")) {
            throw new IllegalArgumentException("Invalid asset id");
        }
    }

    @JsonValue
    public String id() {
        return id;
    }
}
