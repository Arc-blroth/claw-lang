package ai.arcblroth.claw.compiler

import ai.arcblroth.claw.antlr.ClawASTBuilderVisitor
import ai.arcblroth.claw.antlr.ClawLexer
import ai.arcblroth.claw.antlr.ClawParser
import ai.arcblroth.claw.compiler.exceptions.CompilerException
import ai.arcblroth.claw.compiler.exceptions.ParsingErrorException
import ai.arcblroth.claw.compiler.pass.ClawAST2ScratchProjectVisitor
import ai.arcblroth.claw.compiler.pass.CompilerPass
import ai.arcblroth.claw.compiler.pass.NormalizePass
import ai.arcblroth.claw.scratch.Costume
import ai.arcblroth.claw.scratch.MD5Ext
import ai.arcblroth.claw.scratch.SB3
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.DefaultErrorStrategy
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.tree.ParseTreeWalker
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Represents a compilation unit in the Claw programming language.
 * Calling [compile] wil compile this compilation unit
 * into a Scratch 3.0 project file.
 */
class ClawCompilationUnit(private val input: Reader) : AutoCloseable {
    companion object {
        val COMPILER_VERSION: String = try {
            ClawCompilationUnit::class.java
                .classLoader
                .getResourceAsStream("META-INF/claw-version")
                .use { String(it!!.readAllBytes()) }
        } catch (e: Exception) {
            "unknown"
        }

        private val DEFAULT_COSTUME_ASSET: ByteArray = try {
            ClawCompilationUnit::class.java
                .classLoader
                .getResourceAsStream(Costume.EMPTY_COSTUME.md5ext.id)
                .use { it!!.readAllBytes() }
        } catch (e: Exception) {
            throw RuntimeException("Could not find default costume asset", e)
        }
    }

    private var passes = mutableListOf<CompilerPass>()

    constructor(inputStream: InputStream) : this(InputStreamReader(inputStream)) {
        registerPasses()
    }

    private fun registerPasses() {
        registerPass(NormalizePass())
    }

    fun registerPass(pass: CompilerPass) {
        passes.add(pass)
    }

    /**
     * Compiles this compilation unit into a Scratch project file.
     *
     * @return A string that represents the contents of `project.json`.
     * @throws IOException If an error occurs in reading from the given input.
     */
    @Throws(IOException::class, CompilerException::class)
    fun compile(): SB3 {
        // Parse
        val lexer = ClawLexer(CharStreams.fromReader(input))
        val parser = ClawParser(CommonTokenStream(lexer))
        val errorInParsing = AtomicBoolean(false)
        parser.errorHandler = object : DefaultErrorStrategy() {
            override fun reportError(recognizer: Parser?, e: RecognitionException?) {
                errorInParsing.set(true)
                super.reportError(recognizer, e)
            }
        }
        val tree = parser.compilationUnit()
        if (errorInParsing.get()) {
            throw ParsingErrorException()
        }

        // Build AST
        val walker = ParseTreeWalker()
        val astBuilder = ClawASTBuilderVisitor()
        walker.walk(astBuilder, tree)
        val ast = astBuilder.toAST()

        // Execute passes
        passes.forEach { it.visitAST(ast) }

        // Convert AST to SB3
        val projectBuilder = ClawAST2ScratchProjectVisitor()
        projectBuilder.visitAST(ast)
        val project = projectBuilder.toProject()
        val assets = HashMap<MD5Ext, ByteArray>()
        assets[Costume.EMPTY_COSTUME.md5ext] = DEFAULT_COSTUME_ASSET
        return SB3(project, assets)
    }

    @Throws(IOException::class)
    override fun close() {
        input.close()
    }
}
