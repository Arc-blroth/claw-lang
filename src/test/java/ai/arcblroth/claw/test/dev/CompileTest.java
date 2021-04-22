package ai.arcblroth.claw.test.dev;

import ai.arcblroth.claw.compiler.ClawCompilationUnit;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipOutputStream;

public class CompileTest {

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
            var sb3 = new ClawCompilationUnit(fis).compile();
            System.out.println(sb3.getProjectAsJSON(true));

            var fileName = file.getFileName().toString();
            var lastDot = fileName.lastIndexOf('.');
            var fileNameWithoutExt = lastDot == -1 ? fileName : fileName.substring(0, lastDot);
            var outSB3Path = Paths.get("test/out/" + fileNameWithoutExt + ".sb3");
            try (var outSB3 = new ZipOutputStream(Files.newOutputStream(outSB3Path))) {
                sb3.writeToZip(outSB3);
            }
            System.out.println("------------------------------");
            System.out.println("Wrote sb3 to " + outSB3Path.toAbsolutePath());
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
