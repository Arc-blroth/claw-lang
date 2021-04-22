package ai.arcblroth.claw.compiler;

import ai.arcblroth.claw.antlr.ClawASTBuilderVisitor;
import ai.arcblroth.claw.antlr.ClawLexer;
import ai.arcblroth.claw.antlr.ClawParser;
import ai.arcblroth.claw.compiler.pass.ClawAST2ScratchProjectVisitor;
import ai.arcblroth.claw.compiler.pass.CompilerPass;
import ai.arcblroth.claw.compiler.pass.NormalizePass;
import ai.arcblroth.claw.scratch.Costume;
import ai.arcblroth.claw.scratch.MD5Ext;
import ai.arcblroth.claw.scratch.SB3;
import ai.arcblroth.claw.util.ArrayListQueue;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Represents a compilation unit in the Claw programming language.
 * Calling {@link #compile()} wil compile this compilation unit
 * into a Scratch 3.0 project file.
 */
public class ClawCompilationUnit implements AutoCloseable {

    public static final String COMPILER_VERSION;
    private static final byte[] DEFAULT_COSTUME_ASSET;

    static {
        String tempCompilerVersion;
        try (var stream = ClawCompilationUnit.class
                .getClassLoader()
                .getResourceAsStream("META-INF/claw-version")
        ) {
            tempCompilerVersion = new String(stream.readAllBytes());
        } catch (IOException e) {
            tempCompilerVersion = "unknown";
        }
        COMPILER_VERSION = tempCompilerVersion;

        try (var stream = ClawCompilationUnit.class
                .getClassLoader()
                .getResourceAsStream(Costume.EMPTY_COSTUME.md5ext().id())
        ) {
            DEFAULT_COSTUME_ASSET = stream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Could not find default costume asset", e);
        }
    }

    private final Reader input;
    private ArrayList<CompilerPass> passes;

    public ClawCompilationUnit(InputStream inputStream) {
        this.input = new InputStreamReader(Objects.requireNonNull(inputStream));
        registerPasses();
    }

    public ClawCompilationUnit(Reader input) {
        this.input = Objects.requireNonNull(input);
        registerPasses();
    }

    private void registerPasses() {
        this.passes = new ArrayListQueue<>();
        registerPass(new NormalizePass());
    }

    public void registerPass(CompilerPass pass) {
        this.passes.add(Objects.requireNonNull(pass));
    }

    /**
     * Compiles this compilation unit into a Scratch project file.
     *
     * @return A string that represents the contents of <code>project.json</code>.
     * @throws IOException If an error occurs in reading from the given input.
     */
    public SB3 compile() throws IOException {
        // Parse
        var lexer = new ClawLexer(CharStreams.fromReader(this.input));
        var parser = new ClawParser(new CommonTokenStream(lexer));
        var tree = parser.compilationUnit();

        // Build AST
        var walker = new ParseTreeWalker();
        var astBuilder = new ClawASTBuilderVisitor();
        walker.walk(astBuilder, tree);
        var ast = astBuilder.toAST();

        // Execute passes
        this.passes.forEach(p -> p.visitAST(ast));

        // Convert AST to SB3
        var projectBuilder = new ClawAST2ScratchProjectVisitor();
        projectBuilder.visitAST(ast);
        var project = projectBuilder.toProject();

        var assets = new HashMap<MD5Ext, byte[]>();
        assets.put(Costume.EMPTY_COSTUME.md5ext(), DEFAULT_COSTUME_ASSET);
        return new SB3(project, assets);
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

}
