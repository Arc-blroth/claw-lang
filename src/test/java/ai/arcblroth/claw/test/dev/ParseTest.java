package ai.arcblroth.claw.test.dev;

import ai.arcblroth.claw.antlr.ClawLexer;
import ai.arcblroth.claw.antlr.ClawParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ParseTest {

    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Usage: pass a .claw file as the first argument.");
            return;
        }
        var file = Paths.get(args[0]);
        if (!Files.exists(file)) {
            System.err.printf("File %s doesn't exist!\n", args[0]);
            return;
        }

        try (var fis = Files.newInputStream(file)) {
            var input = CharStreams.fromStream(fis);
            var parser = new ClawParser(new CommonTokenStream(new ClawLexer(input)));
            var tree = parser.compilationUnit();
            System.out.println(tree.toStringTree(parser));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
