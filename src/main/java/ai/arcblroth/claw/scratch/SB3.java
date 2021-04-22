package ai.arcblroth.claw.scratch;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A Scratch 3.0 SB3 file. Contains `project.json` and all assets.
 */
public record SB3(Project project, HashMap<MD5Ext, byte[]> assets) {

    /**
     * Converts the {@link #project()} to a JSON object.
     *
     * @return This SB3's project as a JSON object, in the same format used in <code>project.json</code>
     */
    public String getProjectAsJSON(boolean pretty) {
        try {
            var mapper = newMapperForProjectSerialization();
            if (pretty) {
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(project);
            } else {
                return mapper.writer().writeValueAsString(project);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not serialize project", e);
        }
    }

    /**
     * Archives this SB3 for writing to disk.
     * Does not close the provided stream.
     *
     * @param zip Zip file to write to.
     * @throws IOException if an error occurs in serializing the project or in archiving files.
     */
    public void writeToZip(ZipOutputStream zip) throws IOException {
        // Write project.json
        zip.putNextEntry(new ZipEntry("project.json"));
        var mapper = newMapperForProjectSerialization();
        mapper.writer().writeValue(zip, project);
        zip.closeEntry();

        for (var asset : assets.entrySet()) {
            zip.putNextEntry(new ZipEntry(asset.getKey().id()));
            zip.write(asset.getValue(), 0, asset.getValue().length);
            zip.closeEntry();
        }

        zip.finish();
    }

    private static ObjectMapper newMapperForProjectSerialization() {
        var mapper = new ObjectMapper();
        mapper.setConfig(mapper.getSerializationConfig().without(JsonGenerator.Feature.AUTO_CLOSE_TARGET));
        mapper.registerModule(new Jdk8Module());
        return mapper;
    }
}