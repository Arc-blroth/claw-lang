use std::collections::HashMap;
use std::rc::Rc;

use serde_json::Value;

use crate::parser::{Program, Sprite};
use crate::project::{Costume, Project, ProjectMeta, Target};

pub fn emit_code(mut program: Program) -> String {
    let mut out = Project {
        targets: vec![],
        meta: ProjectMeta {
            semver: String::from("3.0.0"),
            vm: String::from("0.2.0-claw"),
            agent: String::from("Claw/0.0.1")
        }
    };
    out.targets.push(emit_sprite(program.sprites.remove("Stage").unwrap_or(Sprite {
        name: String::from("Stage")
    })));
    for sprite in program.sprites.drain() {
        out.targets.push(emit_sprite(sprite.1));
    }
    serde_json::to_string_pretty(&out).unwrap()
}

pub fn emit_sprite(sprite: Sprite) -> Target {
    Target {
        is_stage: sprite.name == "Stage",
        name: sprite.name,
        current_costume: 0,
        costumes: vec![default_costume()],
        blocks: HashMap::new(),
        sounds: vec![],
        variables: HashMap::new()
    }
}

fn default_costume() -> Costume {
    Costume {
        asset_id: String::from("cd21514d0531fdffb22204e0ec5ed84a"),
        name: String::from("Default Costume"),
        md5ext: String::from("cd21514d0531fdffb22204e0ec5ed84a.svg"),
        data_format: String::from("svg"),
        rotation_center_x: 240,
        rotation_center_y: 180,
    }
}