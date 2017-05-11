# Praso - A Design Pattern Detection Tool

Automatically detect software design patterns by constraint satisfaction as specified by User


### Install & Run
- Clone repo: `git clone https://github.com/justiceo/praso-dpd`
- Install JDK, Scala, SBT (if not already installed): `cd praso-dpd; sudo ./install.sh`
- Run: `sbt run` or `sbt "run-main DPD.ScalaREPL"`
- Run (specify dsm): `sbt "run-main DPD.ScalaREPL dsm/simpleObserverPattern.dsm"`
- To run unit tests: `sbt test`

### Todo
###### Real todo
- Inspect vetted design pattern implementations (jhotdraw, java-design-patterns, head-first, gof)
- Allow annotation of dsm files with descriptions
- Create rules for as many patterns as possible
- Implement scoring system
- For a given project, run all the patterns against it and return result


###### Fancy todo
- Configure prompt to display current path with "DPD "
- Allow parsing default dsm as argument to `sbt run`
- Create pattern definitions as scala scripts that use DPD, instead of being part of DPD (this would allow easy mods)
- Add function dsm to the mix
- Use machine learning to detect observer pattern
- Integrate the bash shell for more native opeartions
- Generate dsm and func from source using Understand
- Parse source files for additional meta-data
