use std::collections::HashMap;

use serde::de::{Error, Visitor};
use serde::ser::{SerializeSeq, SerializeStruct, SerializeTuple};
use serde::{Deserializer, Serialize, Serializer};

#[derive(Serialize)]
pub struct Project {
    pub targets: Vec<Target>,
    pub meta: ProjectMeta,
}

#[derive(Serialize)]
pub struct ProjectMeta {
    pub semver: String,
    pub vm: String,
    pub agent: String,
}

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
pub struct Target {
    pub is_stage: bool,
    pub name: String,
    pub current_costume: u32,
    pub costumes: Vec<Costume>,
    pub variables: HashMap<String, Variable>,
    pub sounds: Vec<Sound>,
    pub blocks: Blocks,
}

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
pub struct Costume {
    pub asset_id: String,
    pub name: String,
    pub md5ext: String,
    pub data_format: String,
    pub rotation_center_x: i32,
    pub rotation_center_y: i32,
}

impl Default for Costume {
    fn default() -> Self {
        Self {
            asset_id: "cd21514d0531fdffb22204e0ec5ed84a".to_string(),
            name: "Default Costume".to_string(),
            md5ext: "cd21514d0531fdffb22204e0ec5ed84a.svg".to_string(),
            data_format: "svg".to_string(),
            rotation_center_x: 240,
            rotation_center_y: 180,
        }
    }
}

pub struct Variable {
    pub name: String,
    pub value: String,
    pub global: bool,
}

impl Serialize for Variable {
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: Serializer,
    {
        if self.global {
            (&self.name, &self.value, &self.global).serialize(serializer)
        } else {
            (&self.name, &self.value).serialize(serializer)
        }
    }
}

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
pub struct Sound {}

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
pub struct Block {
    pub opcode: String,
    pub next: Option<String>,
    pub parent: Option<String>,
    pub top_level: bool,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub inputs: Option<Inputs>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub mutation: Option<Mutation>,
}

#[derive(Serialize)]
#[serde(rename_all = "snake_case")]
pub struct Inputs {
    pub custom_block: CustomBlock,
}

pub struct CustomBlock {
    pub shadow: u8,
    pub prototype: u64,
}

impl Serialize for CustomBlock {
    fn serialize<S>(&self, serializer: S) -> Result<<S as Serializer>::Ok, <S as Serializer>::Error>
    where
        S: Serializer,
    {
        let mut seq = serializer.serialize_seq(Some(2))?;
        seq.serialize_element(&self.shadow)?;
        seq.serialize_element(self.prototype.to_string().as_str())?;
        seq.end()
    }
}

pub struct Mutation {
    pub proccode: String,
    pub warp: bool, // run without screen refresh
}

impl Serialize for Mutation {
    fn serialize<S>(&self, serializer: S) -> Result<<S as Serializer>::Ok, <S as Serializer>::Error>
    where
        S: Serializer,
    {
        let mut struc = serializer.serialize_struct("Mutation", 4)?;
        struc.serialize_field("tagName", "mutation")?;
        struc.serialize_field("children", &Vec::<()>::new())?;
        struc.serialize_field("proccode", self.proccode.as_str())?;
        struc.serialize_field("argumentids", "[]")?;
        struc.serialize_field("argumentnames", "[]")?;
        struc.serialize_field("argumentdefaults", "[]")?;
        struc.serialize_field("warp", &self.warp)?;
        struc.end()
    }
}

#[derive(Serialize)]
#[serde(transparent)]
pub struct Blocks {
    pub inner: HashMap<u64, Block>,
    #[serde(skip)]
    id_counter: u64,
}

impl Blocks {
    pub fn new() -> Self {
        Self {
            inner: HashMap::new(),
            id_counter: 0,
        }
    }

    pub fn push_block(&mut self, block: Block) -> u64 {
        let id = self.id_counter;
        self.inner.insert(id, block);
        self.id_counter += 1;
        id
    }
}
