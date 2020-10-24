use std::collections::HashMap;

use serde::{Deserialize, Deserializer, Serialize, Serializer};
use serde::de::Error;
use serde::ser::SerializeTuple;

#[derive(Serialize, Deserialize)]
pub struct Project {
    pub targets: Vec<Target>,
    pub meta: ProjectMeta,
}

#[derive(Serialize, Deserialize)]
pub struct ProjectMeta {
    pub semver: String,
    pub vm: String,
    pub agent: String,
}

#[derive(Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct Target {
    pub is_stage: bool,
    pub name: String,
    pub current_costume: u32,
    pub costumes: Vec<Costume>,
    pub variables: HashMap<String, Variable>,
    pub sounds: Vec<Sound>,
    pub blocks: HashMap<String, Block>,
}

#[derive(Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct Costume {
    pub asset_id: String,
    pub name: String,
    pub md5ext: String,
    pub data_format: String,
    pub rotation_center_x: i32,
    pub rotation_center_y: i32,
}

pub struct Variable {
    pub name: String,
    pub value: String,
    pub global: bool,
}

impl Serialize for Variable {
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error> where
        S: Serializer {
        if self.global {
            (&self.name, &self.value, &self.global).serialize(serializer)
        } else {
            (&self.name, &self.value).serialize(serializer)
        }
    }
}

impl<'de> Deserialize<'de> for Variable {
    fn deserialize<D>(deserializer: D) -> Result<Variable, D::Error> where
        D: Deserializer<'de> {
        Err(D::Error::custom("unimplemented"))
    }
}

#[derive(Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct Sound {

}

#[derive(Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct Block {

}