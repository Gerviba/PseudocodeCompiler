PSeudoCode-Compiler
===

## Telepítés

Jelenleg csak forrásból lehet telepíteni. Minimum Java JRE 8 szükséges a futtatáshoz.

> Az integrációs tesztekhez jelenleg Linuxra van szükség. (Vagy bármire ami támogatja a bash scripteket.)

#### Telepítés Linuxon forrásból

Futtasd a maven builet `clean install` góllal, majd indítsd el a `install/install.sh`-t.

#### Telepítés Windowson forrásból

Futtasd a maven builet `clean install` góllal, majd indítsd el a `install/install.bat` scriptet.

#### Manuális telepítés

Helyezd a `psre.jar` fájlt olyan helyre, ami a PATH környezeti változóban található, vagy az itt felsoroltak körül valahol: (Operációsrendszertől függően)

- `/bin`
- `/sbin`
- `/usr/bin`
- `/usr/sbin`
- `C:\Windows`

## Használat

`$ psre [fájl] [Beállítások és IO beállítások]`   

#### Beállítások:

|Kapcsoló|Hatás|
|---|---|
|-c, --compile           |A bemeneti forrást a fordító fordítsa le|
|-r, --run               |Futtassa le a programot (*1)|
|-d, --debug             |Debugger indítása, hibakeresési mód|
|-s, --semi-compressed   |Félig tömörített mód (alapértelmezett)|
|-f, --fully-compressed  |Teljesen tömörített mód (beta)|
|-h, --help              |Kiírja ezt a táblázatot|
|-i, --info              |Kiírja a megadott fájl meta adatait|
|-v, --version           |Kiírja a telepített psre verzióját|
|--nogui                 |Kikapcsolja a grafikus funkciókat|
|--jailed                |Engedélyez minden olyan IOstreamet ami egy mappában van a forrásfájlal, viszont tilt minden mást.|

> *1: Opcionális ha van `-s` vagy `-f` kapcsoló  

#### IO Beállítások:

|Kapcsoló|Hatás|
|---|---|
|--input-file       |Fájlból olvas be|
|--input-base64     |Base64 kódot olvas be (beta)|
|--input-stdin      |A szabvány bemeneten (stdin) olvas be|
|--output-file      |Fájlba menti a kimenetet|
|--output-stdout    |A szabvány kimeneten (stdout) írja ki a kiemnetet|                      

#### Lefordított kód indítása Linuxon

Használd a `./fájl_neve.psc` parancsot. Az első sor miatt automatikusan a telepített psre-hez fogja társítani.

## Példa programok

Példa programok hamarosan...

## Trello

A projekt trello táblája: [Trello](https://trello.com/b/foojaUu4/pseudocode-compiler)

## Web Interfész

[Itt elérhető](https://github.com/gerviba/psc-online/)