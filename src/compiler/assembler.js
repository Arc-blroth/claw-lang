const fs = require("fs");
const VirtualMachine = require("scratch-vm");
const Sprite = require("../../node_modules/scratch-vm/src/sprites/sprite.js");
const StageLayering = require("../../node_modules/scratch-vm/src/engine/stage-layering.js");
const JSZip = require("node-zip");
const BlockFactory = require("./block_factory.js");

const blankCostume = {
    assetId: "cd21514d0531fdffb22204e0ec5ed84a",
    name: "Blank",
    md5ext: "cd21514d0531fdffb22204e0ec5ed84a.svg",
    dataFormat: "svg",
    rotationCenterX: 240,
    rotationCenterY: 180
};
const blankCostumeSvg = fs.readFileSync("res/" + blankCostume.md5ext);

class Assembler {
    
    constructor() {
        this.vm = new VirtualMachine();
        let runtime = this.vm.runtime;
        
        this.stage = new Sprite(null, runtime);
        this.stage.addCostumeAt(blankCostume, 0);
        let stageTarget = this.stage.createClone();
        stageTarget.isStage = true;
        runtime.addTarget(stageTarget, StageLayering.BACKGROUND_LAYER);
        
        this.blockFactory = new BlockFactory();
        this.sprites = {};
        this.currentSprite = null;
        this.lastBlock = null;
    }
    
    pushSprite(name) {
        if(this.currentSprite !== null) throw "Cannot push a sprite inside of another sprite!";
        this.currentSprite = name;
        this.sprites[name] = new Sprite(null, this.vm.runtime);
        this.sprites[name].addCostumeAt(blankCostume, 0);
    }
    
    popSprite() {
        if(this.currentSprite === null) throw "No sprite to pop!";
        this.vm.runtime.addTarget(this.sprites[this.currentSprite].createClone());
        this.currentSprite = null;
    }
    
    pushFunction(funName) {
        if(this.lastBlock !== null) throw "Cannot push a function inside of another function!";
        if(funName == "onGreenFlag") {
            this.lastBlock = this.blockFactory.buildBlock("event_whenflagclicked");
        }
    }
    
    popFunction() {
        if(this.lastBlock === null) throw "No function to pop!";
        this.sprites[this.currentSprite].blocks.createBlock(this.lastBlock);
        this.lastBlock = null;
    }
    
    pushBlock(opcode, args) {
        if(this.lastBlock === null) throw "Cannot push a block while outside a function!";
        let nextBlock = this.blockFactory.buildBlock(opcode, args);
        this.blockFactory.link(this.lastBlock, nextBlock);
        this.sprites[this.currentSprite].blocks.createBlock(this.lastBlock);
        this.lastBlock = nextBlock;
    }
    
    assemble() {
        let zip = new JSZip();
        let projectJson = this.vm.toJSON();
        zip.file("project.json", projectJson);
        zip.file(blankCostume.md5ext, blankCostumeSvg);

        fs.mkdirSync("out", { recursive: true });
        fs.writeFile("out/project.sb3", zip.generate({base64: false, compression: "DEFLATE"}), "binary", (err) => {
          if (err) return console.log(err);
          console.log("Successfully saved test project");
        });
        fs.writeFile("out/project-source.json", JSON.stringify(JSON.parse(projectJson), null, 4), "utf8", (err) => {
          if (err) return console.log(err);
          console.log("Successfully saved test project source");
        });
    }
    
}

module.exports = Assembler;