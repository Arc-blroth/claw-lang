use std::collections::HashMap;
use std::rc::Rc;

use serde_json::Value;

use crate::parser::{Program, Sprite};
use crate::project::{Costume, Project, ProjectMeta, Target};

pub fn emit_code(mut program: Program) -> String {
    let mut out = Project {
        targets: vec![],
        meta: ProjectMeta {
            semver: "3.0.0".to_string(),
            vm: "0.2.0-claw".to_string(),
            agent: "Claw/0.0.1".to_string()
        }
    };
    out.targets.push(emit_sprite(program.sprites.remove("Stage").unwrap_or(Sprite {
        name: "Stage".to_string()
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
        asset_id: "cd21514d0531fdffb22204e0ec5ed84a".to_string(),
        name: "Default Costume".to_string(),
        md5ext: "cd21514d0531fdffb22204e0ec5ed84a.svg".to_string(),
        data_format: "svg".to_string(),
        rotation_center_x: 240,
        rotation_center_y: 180,
    }
}