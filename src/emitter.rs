use std::collections::HashMap;
use std::rc::Rc;

use serde_json::Value;

use crate::parser::{Function, Program, Sprite};
use crate::project::{
    Block, Blocks, Costume, CustomBlock, Inputs, Mutation, Project, ProjectMeta, Target,
};

pub fn emit_code(mut program: Program) -> String {
    let mut out = Project {
        targets: vec![],
        meta: ProjectMeta {
            semver: "3.0.0".to_string(),
            vm: "0.2.0-claw".to_string(),
            agent: "Claw/0.0.1".to_string(),
        },
    };
    out.targets
        .push(emit_sprite(program.sprites.remove("Stage").unwrap_or(
            Sprite {
                name: "Stage".to_string(),
                functions: vec![],
            },
        )));
    for sprite in program.sprites.drain() {
        out.targets.push(emit_sprite(sprite.1));
    }
    serde_json::to_string_pretty(&out).unwrap()
}

pub fn emit_sprite(sprite: Sprite) -> Target {
    let mut blocks = Blocks::new();
    for f in sprite.functions {
        emit_function(&mut blocks, f);
    }
    Target {
        is_stage: sprite.name == "Stage",
        name: sprite.name,
        current_costume: 0,
        costumes: vec![Costume::default()],
        blocks,
        sounds: vec![],
        variables: HashMap::new(),
    }
}

pub fn emit_function(blocks: &mut Blocks, function: Function) {
    let proto = blocks.push_block(Block {
        opcode: "procedures_prototype".to_string(),
        next: None,
        parent: None,
        inputs: None,
        mutation: Some(Mutation {
            proccode: function.name,
            warp: true,
        }),
        top_level: false,
    });
    let def = blocks.push_block(Block {
        opcode: "procedures_definition".to_string(),
        next: None,
        parent: None,
        inputs: Some(Inputs {
            custom_block: CustomBlock {
                shadow: 1,
                prototype: proto,
            },
        }),
        mutation: None,
        top_level: true,
    });
    blocks.inner.get_mut(&proto).unwrap().parent = Some(def.to_string());
}
