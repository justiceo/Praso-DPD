# Praso - A Design Pattern Detection Tool

Automatically detect software design patterns by constraint satisfaction using an interactive shell.

This project depends on [Scitools Understand](https://scitools.com/features) for generating dependency matrices.
There's also a free [student licence](https://scitools.com/student/)

The interactive shell uses [Ammonite](http://www.lihaoyi.com/Ammonite/ m), which not only makes it possible to run the program interatively, but brings the capabilities of scala and bash into the mix.

### Install & Run
- Clone repo: `git clone https://github.com/justiceo/praso-dpd`
- Install JDK, Scala, SBT (if not already installed): `cd praso-dpd; sudo ./install.sh`
- Run: `sbt run` or `sbt "run-main DPD.ScalaREPL"`
- Run (specify dsm): `sbt "run-main DPD.ScalaREPL dsm/simpleObserverPattern.dsm"`
- To run unit tests: `sbt test`

### More on Running
To find the patterns in a project X
- first download the source code for the project by running `download -github https://github.com/test/projectX`
- generate the dsm files and other stuff by running `analyze projectX`

##### Add Scala Doc Instead


### Shell variables
In addition to the variables made available by ammonite shell, the following are included
- dsm: holds the representation of the dsm file
    - to view entire dsm run `show(dsm)`
    - .nice: prints an enhanced version of the dsm
- $: a handle for the util class
    - .parse(dsm_file_path): loads the given dsm
    - .resource(rel_path): get the full path of a resource in the resource dir
    - .export(dsm, filepath): save the dsm to the file path given
    - .gitClone(url): download the github repo to the target dir
    - .genDsm(project_path): generates dsm for given project


### Todo
###### Meat
- Inspect vetted design pattern implementations (jhotdraw, java-design-patterns, head-first, gof)
- Create rules for as many patterns as possible
- Add discriminatory rules to the patterns to reduce false positives
- Add print pocket method that prints the list in pockets
- Implement scoring system
- Try the new patterns from p.c.
- Add more methods for reconciling pockets

###### Spice
- Add function dsm to the mix
- Generate dsm and func from source using Understand
- Create perl script for generating class dsms with extra sugar, then update DsmDS
- Parse source files for additional meta-data


optimizations
- string paddTo
- 0-range).indices instead of zipWithIndex
