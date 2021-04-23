package ai.arcblroth.claw.scratch

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * A Scratch 3.0 SB3 file. Contains `project.json` and all assets.
 */
data class SB3(val project: Project, val assets: HashMap<MD5Ext, ByteArray>) {
    /**
     * Converts the [project] to a JSON object.
     *
     * @return This SB3's project as a JSON object, in the same format used in <code>project.json</code>
     */
    fun getProjectAsJSON(pretty: Boolean): String? {
        return try {
            val mapper = newMapperForProjectSerialization()
            if (pretty) {
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(project)
            } else {
                mapper.writer().writeValueAsString(project)
            }
        } catch (e: JsonProcessingException) {
            throw RuntimeException("Could not serialize project", e)
        }
    }

    /**
     * Archives this SB3 for writing to disk.
     * Does not close the provided stream.
     *
     * @param zip Zip file to write to.
     * @throws IOException if an error occurs in serializing the project or in archiving files.
     */
    @Throws(IOException::class)
    fun writeToZip(zip: ZipOutputStream) {
        // Write project.json
        zip.putNextEntry(ZipEntry("project.json"))
        val mapper = newMapperForProjectSerialization()
        mapper.writer().writeValue(zip, project)
        zip.closeEntry()
        for ((key, value) in assets) {
            zip.putNextEntry(ZipEntry(key.id))
            zip.write(value, 0, value.size)
            zip.closeEntry()
        }
        zip.finish()
    }

    private fun newMapperForProjectSerialization(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.setConfig(mapper.serializationConfig.without(JsonGenerator.Feature.AUTO_CLOSE_TARGET))
        mapper.registerModule(Jdk8Module())
        return mapper
    }
}
