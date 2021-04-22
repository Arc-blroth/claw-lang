package ai.arcblroth.claw.compiler.pass;

import ai.arcblroth.claw.compiler.ast.ClawAST;
import ai.arcblroth.claw.compiler.ast.ClawASTVisitor;
import ai.arcblroth.claw.compiler.ast.SpriteNode;
import ai.arcblroth.claw.compiler.ast.StageNode;
import ai.arcblroth.claw.scratch.Costume;
import ai.arcblroth.claw.scratch.Project;
import ai.arcblroth.claw.scratch.Sprite;
import ai.arcblroth.claw.scratch.Stage;
import ai.arcblroth.claw.util.ArrayListQueue;

import java.util.HashMap;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class ClawAST2ScratchProjectVisitor extends ClawASTVisitor {

    private Project out;

    @Override
    public void visitAST(ClawAST ast) {
        this.out = new Project(
                new Project.Meta(
                        "3.0.0",
                        "0.2.0-claw",
                        "Claw/0.0.1"
                ),
                new ArrayListQueue<>()
        );
        super.visitAST(ast);
    }

    @Override
    public void visitStage(StageNode stageNode) {
        var costumes = new ArrayListQueue<Costume>();
        costumes.add(Costume.EMPTY_COSTUME);
        out.targets().add(new Stage(
                0,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                costumes,
                new ArrayListQueue<>(),
                OptionalDouble.empty(),
                OptionalDouble.empty(),
                OptionalDouble.empty(),
                Optional.empty(),
                OptionalInt.empty()
        ));
        super.visitStage(stageNode);
    }

    @Override
    public void visitSprite(SpriteNode spriteNode) {
        var costumes = new ArrayListQueue<Costume>();
        costumes.add(Costume.EMPTY_COSTUME);
        out.targets().add(new Sprite(
                0,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                costumes,
                new ArrayListQueue<>(),
                OptionalDouble.empty(),
                spriteNode.name(),
                Optional.of(true),
                OptionalDouble.empty(),
                OptionalDouble.empty(),
                OptionalDouble.empty(),
                OptionalDouble.empty(),
                Optional.of(false),
                Sprite.RotationStyle.ALL_AROUND,
                OptionalInt.empty()
        ));
        super.visitSprite(spriteNode);
    }

    public Project toProject() {
        return out;
    }

}
