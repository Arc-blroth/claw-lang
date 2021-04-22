package ai.arcblroth.claw.compiler.ast;

import ai.arcblroth.claw.util.ArrayListQueue;

public class ClawAST {

    public StageNode stage;
    public ArrayListQueue<SpriteNode> sprites;

    public ClawAST() {
        this.stage = null;
        this.sprites = new ArrayListQueue<>();
    }

}
