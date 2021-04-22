package ai.arcblroth.claw.scratch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.OptionalInt;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Costume(
        @JsonProperty(value = "assetId", required = true)
        AssetId assetId,

        OptionalInt bitmapResolution,

        @JsonProperty(value = "dataFormat", required = true)
        DataFormat dataFormat,

        MD5Ext md5ext,

        @JsonProperty(value = "name", required = true)
        String name,

        int rotationCenterX,
        int rotationCenterY
) {
    public enum DataFormat {
        @JsonProperty("png") PNG,
        @JsonProperty("svg") SVG,
        @JsonProperty("jpeg") JPEG,
        @JsonProperty("jpg") JPG,
        @JsonProperty("bmp") BMP,
        @JsonProperty("gif") GIF
    }

    public static final Costume EMPTY_COSTUME = new Costume(
            new AssetId("cd21514d0531fdffb22204e0ec5ed84a"),
            OptionalInt.empty(),
            DataFormat.SVG,
            new MD5Ext("cd21514d0531fdffb22204e0ec5ed84a.svg"),
            "Default Costume",
            240,
            180
    );
}
