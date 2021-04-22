package ai.arcblroth.claw.compiler.pass;

import ai.arcblroth.claw.compiler.ast.ClawAST;
import ai.arcblroth.claw.compiler.ast.StageNode;
import ai.arcblroth.claw.util.ArrayListQueue;

/**
 * Initial pass to add defaults.
 */
public class NormalizePass extends CompilerPass {

    public void visitAST(ClawAST ast) {
        if (ast.stage == null) {
            ast.stage = new StageNode(new ArrayListQueue<>());
        }
        super.visitAST(ast);
    }

}
