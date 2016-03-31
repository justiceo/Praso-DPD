# Praso - A Design Pattern Detection Tool

Automatically detect software design patterns by contraint satisfaction as specified by User


### Installing in Eclipse

- Download and extract the project to working directory
- Navigate to File -> Import
- Select Maven -> Existing Maven Project
- Select the directory of the DPD project (should display the pom.xml file)
- And then finish


### Installing on Intellij Idea

- After extracting the project
- Simply open as an intellij project.
- Enable Auto Import dependencies

### Big Todos

- Change all dsm to use relative paths and include list of source locations.
- Develop the input module that takes code location as input, fetch sources and generate dsm' for sources.
- Develop output module to display/print to file the resulting patterns neatly

### Version 3 Goals

- To allow representations of abstract relations like Specialize, realize, abstraction and concrete
- Hence, allow pattern variations. For a particular pattern, run all variations
- Allow efficient and scalable access and processing of queries on dependency
- Allow easy modification and extension of code-snippets.
- All rules to be pattern and language agnostic (java for now).
- Allow the application of rules as scores

#### dsmx format
fileName :: classType :: DependencyLine

#### dsm format
dependencyLine
fileName
