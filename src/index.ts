const fs = require('fs');
const VirtualMachine = require('scratch-vm');
const Sprite = require('../node_modules/scratch-vm/src/sprites/sprite.js');
const StageLayering = require('../node_modules/scratch-vm/src/engine/stage-layering.js');
const JSZip = require('node-zip');
const vm = new VirtualMachine();
const runtime = vm.runtime;

const blankCostume = {
	assetId: 'cd21514d0531fdffb22204e0ec5ed84a',
	name: 'Blank',
	md5ext: 'cd21514d0531fdffb22204e0ec5ed84a.svg',
	dataFormat: 'svg',
	rotationCenterX: 240,
	rotationCenterY: 180
};
const blankCostumeSvg = fs.readFileSync("src/" + blankCostume.md5ext);

let stage = new Sprite(null, runtime);
stage.addCostumeAt(blankCostume, 0);
let stageTarget = stage.createClone();
stageTarget.isStage = true;
runtime.addTarget(stageTarget, StageLayering.BACKGROUND_LAYER);

let sprite1 = new Sprite(null, runtime);
sprite1.addCostumeAt(blankCostume, 0);
runtime.addTarget(sprite1.createClone());

console.log(sprite1.blocks);

let zip = new JSZip();
zip.file('project.json', vm.toJSON());
zip.file(blankCostume.md5ext, blankCostumeSvg);

fs.mkdirSync('out', { recursive: true });
fs.writeFile('out/project.sb3', zip.generate({base64: false, compression: 'DEFLATE'}), 'binary', (err: object) => {
  if (err) return console.log(err);
  console.log('Successfully saved test project');
});