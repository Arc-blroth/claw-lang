package ai.arcblroth.claw.test.dev

import ai.arcblroth.claw.antlr.ClawLexer
import ai.arcblroth.claw.antlr.ClawParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

object ParseTest {
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
                val input = CharStreams.fromStream(fis)
                val parser = ClawParser(CommonTokenStream(ClawLexer(input)))
                val tree = parser.compilationUnit()
                println(tree.toStringTree(parser))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            exitProcess(-1)
        }
    }
}
