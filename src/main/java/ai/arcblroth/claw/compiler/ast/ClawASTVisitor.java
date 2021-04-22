package ai.arcblroth.claw.compiler.ast;

public abstract class ClawASTVisitor {

    public void visitAST(ClawAST ast) {
        if (ast.stage != null) {
            visitStage(ast.stage);
        }
        ast.sprites.forEach(this::visitSprite);
    }

    public void visitStage(StageNode stageNode) {
        visitTarget(stageNode);
    }

    public void visitSprite(SpriteNode spriteNode) {
        visitTarget(spriteNode);
    }

    public void visitTarget(TargetNode targetNode) {
        targetNode.functions().forEach(this::visitFunction);
    }

    public void visitFunction(FunctionNode functionNode) {

    }

}
