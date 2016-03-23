# Automatic Design Pattern Detection

An implementation of the hierarchical pattern definition for automatic pattern detection and isolation

### Installing in Eclipse

- Download and extract the project to working directory
- Navigate to File -> New -> New Project
- Select Maven -> Maven Project
- Select the directory of the DPD project (should display the pom.xml file)
- And then finish


### Installing on Intellij Idea

- After extracting the project
- Simply open as an intellij project.


### Todo

- Change all dsm to use relative paths and include list of source locations. Thorough test the fixPath method.
- Develop the SourceManager module to fetch sources and generate dsm' for sources.
    - automatically fetch from local, git, or ftp location. Run understand to get cyto-scape. Run titan for dsm. Run dsm fixer for cdsm
- design loopFilter to filter through forEach, for, lambda, iterator, listIterator and while loops etc. (and depends on how exhaustive the user wants it)
- migrate sourceParser to Preprocessor slowly, eventually renaming PreProcessor to SourceParser.
- add dependencyLineFilter that converts the zeros to full blown dependency lines without spaces
- move the buildClasses functionality from browser to Preprocessor.
- Convert the entire system into a Pipe and filter.
- Implement A -> B; B -> C :- A -> C for layered abstraction.
- move all Logging to java native logging.
- Add Claims to pattern component. So filters, resolvers and ast-analyzers can add information about why they chose or didn't choose something
- Move dependencies on commons-lang3 to java 8

### Version 3 Goals

- To allow representations of abstract relations like Specialize, realize, abstraction and concrete
- Hence, allow pattern variations. For a particular pattern, run all variations
- Allow efficient and scalable access and processing of queries on dependency
- Allow easy modification and extension of code-snippets.
- All rules to be pattern and language agnostic (java for now).
- Allow the application of rules as scores
- Allow definition of rule starting from user's most convenient point - from pattern-component to simply entity


#### dsmx format
fileName :: classType :: DependencyLine

#### dsm format
dependencyLine
fileName
