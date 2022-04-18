# Pawn for Java!

Pawn for Java (Pawn4j) is a hobby test project if it is possible to implement the Pawn AMX runtime in Java (https://www.compuphase.com/pawn/pawn.htm).

## Pawn Runtime Version

The target runtime version of pawn is the same that **SA:MP** uses.

Not all instructions are implemented yet.
Current working example is below.

## How to use

```
Amx amx = new Amx();
Script timertest = amx.loadFromFile("pawn_example/example.amx");
timertest.executePublic("OnGameModeInit");
```

The main function of the given script is executed when the script is loaded.

The default `BuiltInRuntimeFunctionsPlugin` with its natives from `pawn4j.inc`is implicit registered.

## How to script
The file has to be compiled in 32-bit Pawn mode and use the command line:
`-C- -O0 -d0`

## Developing plugins
You can register any plugin with `amx.loadPlugin(Plugin plugin)`.

Your plugin has to implement the interface `sh.insane.pawn.interop.Plugin`.