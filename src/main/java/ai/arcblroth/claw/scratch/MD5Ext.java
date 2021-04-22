package ai.arcblroth.claw.scratch;

import com.fasterxml.jackson.annotation.JsonValue;

public record MD5Ext(String id) {
    public MD5Ext {
        if (!id.matches("^[a-fA-F0-9]{32}\\.[a-zA-Z]+$")) {
            throw new IllegalArgumentException("Invalid md5ext");
        }
    }

    @JsonValue
    public String id() {
        return id;
    }
}
