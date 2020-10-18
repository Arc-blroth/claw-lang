# Claw Core Specification
### Version 0.1.0

# 1. Introduction
There is no doubt that Scratch has become a center of creativity and learning.

As a programming language, Scratch is designed to be simple to allow people of all ages to learn conceptual programming with few prerequisites.
Yet, as the ever growing collection of public projects on its website show, Scratch can be anything but simple.
From art MAPs (mult-artist projects) to RPGs to recreations of Minecraft and Terraria to three-dimensional platformers,
Scratch have curated a community that constantly pushes the limits of creativity.

Yet, as Scratch projects become increasingly complex, higher-level abstractions and design patterns become increasingly neccessary.

In line with its simplistic nature, Scratch presents a minimal API. 
Through the usage of custom procedures, known as "My Blocks" in the interface, one can extend the API and write "blocks" with custom behaviors.
This translates well to functions in higher-level langauges.
In addition, Scratch also provides two main data structures: variables and lists.
Along with procedures, these structures form the basis for most complex projects.

Yet, Scratch still presents many limitations that are useful in building complex projects.
For example, while Scratch allows one to pass arguments to custom procedures, Scratch does not have a concept of returning variables from a procedure.
Scratch also does not have a concept of local variables or a heap: all variables must be defined when a project begins execution<sup>[1]</sup>,
and creation and deletion of variables is unsupported. All variables are either global to a sprite, or global to the entire project.
Finally, Scratch does not present a builtin error (exception) handling framework.

These above features are present in many modern programming languages, but do not (and likely never will) exist in Scratch, due to their complex nature.
Yet, these features would be highly benefical to those who seek to build complexity from the simplicity of Scratch.

Claw aims to aid the creation of complex Scratch projects, by providing a high-level object-orientated language that transpiles/compiles cleanly to Scratch.
It is an object-orientated, sprite-based, strictly-typed language, and implements many of the missing features that do not exist in Scratch, as well as an expanded standard API based off of ECMAScript.

<hr>
<sup>[1]</sup> The Scratch VM does actually allow the creation of variables at runtime, specifically if a program attempts to read/write to a variable that does not exist. However, this can only be observed by manually modifying the AST of a project to invoke this behavior, and after creation the variable sticks around.

## 1.1 Program Structure and Hello World
Scratch is a sprite-orientated programming langauge, and all code must belong in a sprite (the Stage is also a sprite).
Claw follows this paradigm: each source file consists of "sprite" definitions, similar to javascript's class definitions.
Code is defined in functions inside each sprite.

```clawscript
sprite MyFirstSprite {
    function onGreenFlag() {
        say("Hello World!", 1);
    }
}
```
