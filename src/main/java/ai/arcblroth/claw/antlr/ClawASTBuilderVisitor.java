package ai.arcblroth.claw.antlr;

import ai.arcblroth.claw.compiler.ast.ClawAST;
import ai.arcblroth.claw.compiler.ast.FunctionNode;
import ai.arcblroth.claw.compiler.ast.SpriteNode;
import ai.arcblroth.claw.util.ArrayListQueue;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * Builds a {@link ClawAST} from the ANTLR CST.
 */
public class ClawASTBuilderVisitor extends ClawParserBaseListener {

    private ClawAST ast;

    @Override
    public void enterCompilationUnit(ClawParser.CompilationUnitContext ctx) {
        if (ast != null) {
            throw new ParseCancellationException("Only one compilation unit may be specified in a file");
        }
        ast = new ClawAST();
    }

    @Override
    public void enterSpriteDeclaration(ClawParser.SpriteDeclarationContext ctx) {
        ast.sprites.add(new SpriteNode(ctx.IDENTIFIER().getText(), new ArrayListQueue<>()));
    }

    @Override
    public void enterFunctionDeclaration(ClawParser.FunctionDeclarationContext ctx) {
        ast.sprites.last().functions().add(new FunctionNode(ctx.IDENTIFIER().getText()));
    }

    public ClawAST toAST() {
        return ast;
    }

}
