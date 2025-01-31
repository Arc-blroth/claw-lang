import AudioEngine from "scratch-audio"
import ScratchRender from "scratch-render"
import { ScratchStorage } from "scratch-storage"
import { BitmapAdapter } from "scratch-svg-renderer"
import VirtualMachine from "scratch-vm"
import "./style.css"

// Code mostly adapted from https://github.com/scratchfoundation/scratch-vm/blob/develop/src/playground/benchmark.js
// licensed under the AGPL-3.0.

const vm = new VirtualMachine()
vm.setTurboMode(true)

const storage = new ScratchStorage()
vm.attachStorage(storage)

const canvas = document.getElementById("scratch-stage")
vm.attachRenderer(new ScratchRender(canvas))
vm.attachAudioEngine(new AudioEngine())
vm.attachV2BitmapAdapter(new BitmapAdapter())

document.addEventListener("mousemove", e => {
  const rect = canvas.getBoundingClientRect()
  vm.postIOData("mouse", {
    x: e.clientX - rect.left,
    y: e.clientY - rect.top,
    canvasWidth: rect.width,
    canvasHeight: rect.height
})
})
canvas.addEventListener("mousedown", e => {
  const rect = canvas.getBoundingClientRect()
  vm.postIOData("mouse", {
    isDown: true,
    x: e.clientX - rect.left,
    y: e.clientY - rect.top,
    canvasWidth: rect.width,
    canvasHeight: rect.height
  })
})
canvas.addEventListener("mouseup", e => {
  const rect = canvas.getBoundingClientRect()
  vm.postIOData("mouse", {
    isDown: false,
    x: e.clientX - rect.left,
    y: e.clientY - rect.top,
    canvasWidth: rect.width,
    canvasHeight: rect.height
  })
  e.preventDefault()
})
canvas.addEventListener("keydown", e => {
  vm.postIOData("keyboard", {
      keyCode: e.keyCode,
      isDown: true
  })
  e.preventDefault()
})
canvas.addEventListener("keyup", e => {
  vm.postIOData("keyboard", {
      keyCode: e.keyCode,
      isDown: false
  })
  e.preventDefault()
})

vm.start()

document.getElementById("flag").addEventListener("click", () => {
  vm.greenFlag()
})

document.getElementById("stop").addEventListener("click", () => {
  vm.stopAll()
})

const runSb3Button = document.getElementById("run-sb3")
runSb3Button.value = ""
runSb3Button.addEventListener("change", e => {
  e.preventDefault()
  if (runSb3Button.files.length > 0) {
    runSb3Button.files[0].arrayBuffer().then(data => {
      console.log("Running VM...")
      vm.loadProject(data)
    })
  }
})
