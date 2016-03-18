# Automatic Design Pattern Detection

An implementation of the hierarchical pattern definition for automatic pattern detection and isolation

todo
=========
- design loopFilter to filter through forEach, for, lambda, iterator, listIterator and while loops etc. (and depends on how exhaustive the user wants it)
- migrate sourceParser to Preprocessor slowly, eventually renaming PreProcessor to SourceParser.
- add dependencyLineFilter that converts the zeros to full blown dependency lines without spaces
- add fileNameFilter that cleans the filePath, set the file name, determine if its exist once and for-all (add isAccessible var to jClass). Should be first Filter to run.
- write module for fixig dsm -> to rewrite java class paths to package level and include list of source locations.
- move the buildClasses functionality from browser to Preprocessor.
- Convert the entire system into a Pipe and filter.
- Implement A -> B; B -> C :- A -> C for layered abstraction.

dpd version 3
=============

Goals:
To allow representations of abstract relations like Specialize, realize, abstraction and concrete.
Hence, allow pattern variations. For a particular pattern, run all variations.
Allow efficient and scalable access and storage of files
Allow easy modification of code-snippets; pattern independent and language independent
Allow the application of rules as scores.
Allow executing of pattern specific code.
Allow easy classification of class types as realization, specialization, enum and abstraction.


ClassItem :-                                                                                        -- Done
	classPath      : String
	classId		   : Int (first a string by renaming fileName)
	dependencyLine : String
	classType	   : ClassType

DependencyTypes :- Enum
	DependencyTypes(string[] dependencies)	: void
	getDependencyType(int id)				: DependencyType
	getIndexofDependency(DependencyType d) 	: int

DSMBrowser :-
	all methods are preserved but their implementation reversed.        -- Done
	instead of returning class names, we return their indexes.          -- Done
	add new method getClassPath(int classId) which returns the path.    -- Done


dsmx format
===========
fileName :: classType :: DependencyLine

before reading dsm, see if objectAid file contains all this information.

Add logging information and nicely formatted output - use you logger! Add verbose flag to logger class
Add List<String> claims to entities. So filters, resolvers and ast-analyzers can add information about why they chose or didn't choose something
Put an Id in JClass and substitute fileName for classId.                -- Done
Put an classPath var in JClass.                                         -- Done
Create XDSMBrowser - that extends IBrowser.