package ai.arcblroth.claw.test.dev

import ai.arcblroth.claw.compiler.ClawCompilationUnit
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipOutputStream
import kotlin.system.exitProcess

object CompileTest {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            println("Usage: pass a .claw file as the first argument.")
            return
        }
        val file = Paths.get(args[0])
        if (!Files.exists(file)) {
            System.err.printf("File %s doesn't exist!\n", args[0])
            return
        }

        try {
            Files.newInputStream(file).use { fis ->
                val sb3 = ClawCompilationUnit(fis).compile()
                println(sb3.getProjectAsJSON(true))
                val fileName = file.fileName.toString()
                val lastDot = fileName.lastIndexOf('.')
                val fileNameWithoutExt = if (lastDot == -1) fileName else fileName.substring(0, lastDot)
                val outSB3Path = Paths.get("test/out/$fileNameWithoutExt.sb3")
                ZipOutputStream(Files.newOutputStream(outSB3Path)).use { outSB3 -> sb3.writeToZip(outSB3) }
                println("------------------------------")
                println("Wrote sb3 to " + outSB3Path.toAbsolutePath())
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            exitProcess(-1)
        }
    }
}
