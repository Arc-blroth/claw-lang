package ai.arcblroth.claw.scratch;

import ai.arcblroth.claw.util.ArrayListQueue;

/**
 * Scratch 3.0 Project
 */
public final record Project(Meta meta, ArrayListQueue<Target> targets) {
    public final record Meta(String semver, String vm, String agent) {
    }
}
