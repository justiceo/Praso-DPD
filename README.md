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


### Common REPL variables
In addition to the variables made available by ammonite shell, the following are included


**dsm**

Holds the representation of the dsm file in an adjacency matrix with utility methods for querying dependencies
Sample output of running `dpd@ dsm` (with line numbers included for explanation)
```
0| res0: DPD.DSMDataStructure = [CALL,TYPED,CREATE,IMPLEMENT]
1| 8
2| 0 1100 1010 0 0 0010 0 0
3| 0 0 0 0 0100 0 0 0
4| 0 1111 0 0010 1100 0 0010 0010
5| 0 0001 0 0 1100 0 0 0
6| 0 0 0100 0100 0 0 0100 0100
7| 0 0 0100 0100 0001 0 0100 0100
8| 0 0001 0 0 1100 0 0 0
9| ...
```
* In line 0, `dsm` is copied to a new shell variable `res0`
* It also shows that only 4 kinds of dependencies are exhibited in this dsm: call, typed, create and implement
* Line 1: The number represents the number of files in the dsm, which is also the length and width of the matrix
* Line 2-8: Is the dependency matrix
* Line 9: The `...` means that the entire output was not printed because it was too long
* To view all the output, run `dpd@ show(dsm)`

**dsm.nice**

Prints a visually enhanced version of the dsm
Sample output of running `dpd@ show(dsm.nice)`:
```
"""
[CALL,TYPED,CREATE,IMPLEMENT]
    0    1    2    3    4    5    6    7
--------------------------------------------
0 | 0    1100 1010 0    0    0010 0    0
1 | 0    0    0    0    0100 0    0    0
2 | 0    1111 0    0010 1100 0    0010 0010
3 | 0    0001 0    0    1100 0    0    0
4 | 0    0    0100 0100 0    0    0100 0100
5 | 0    0    0100 0100 0001 0    0100 0100
6 | 0    0001 0    0    1100 0    0    0
7 | 0    0001 0    0    1100 0    0    0
(0) Main.java
(1) ComputerPart.java
(2) Computer.java
(3) Mouse.java
(4) ComputerPartVisitor.java
(5) ComputerPartDisplayVisitor.java
(6) Keyboard.java
(7) Monitor.java
```


**dsm.matrixStr**

Returns a string representation of the adjacency matrix of the dsm as tuples of (dependency data, index) where dependency has been translated from binary string to decimal.
Sample output of running `dpd@ dsm.matrixStr`
```
(12,1)(10,2)(2,5)
(4,4)
(15,1)(2,3)(12,4)(2,6)(2,7)
(1,1)(12,4)
(4,2)(4,3)(4,6)(4,7)
(4,2)(4,3)(1,4)(4,6)(4,7)
(1,1)(12,4)
(1,1)(12,4)
```

**dsm.resolve**

Gets a class index (ID) by it's name. Accepts a variable number of arguments.
Sample output of running `dpd@ dsm.resolve("Computer.java", "ComputerPart.java")`
```
List[Int] = List(1, 2)
```

**dsm.find**

Find classes by keywords. Accepts multiple keywords and any class that matches any of the keywords is included.
Sample output of running `dpd@ dsm.find("visit")`
```
List[String] = List("ComputerPartVisitor.java", "ComputerPartDisplayVisitor.java")
```
With multiple keywords as in `dpd@ dsm.find("mou", "visit")`
```
List[String] = List("Mouse.java", "ComputerPartVisitor.java", "ComputerPartDisplayVisitor.java")
```

**dsm.SPECIALIZE**

Returns a tuple list of class pairs that exhibit EXTEND and/or IMPLEMENT dependency type. To get pairs that exhibit only EXTEND, use **dsm.EXTEND**, and vice-versa for IMPLEMENT. 
(A, B) is same as (A extends B) or (A implements B).
Sample output from running `dpd@ dsm.SPECIALIZE`
```
List[(Int, Int)] = List((2, 1), (3, 1), (5, 4), (6, 1), (7, 1))
```
This can be made more readable by running `dpd@ dsm.nice(dsm.SPECIALIZE)`
```
res6: List[(String, String)] = List(
  ("Computer", "ComputerPart"),
  ("Mouse", "ComputerPart"),
  ("ComputerPartDisplayVisitor", "ComputerPartVisitor"),
  ("Keyboard", "ComputerPart"),
  ("Monitor", "ComputerPart")
)
```

**dsm.subDsm**

Creates a new dsm from a subset of the classes in the current dsm.
Can we run with multiple classes as in `dpd@ dsm.subDsm("Mouse.java", "Computer.java")`. 
Even better using the `dsm.find` above as in `dpd@ dsm.subDsm(dsm.find('visitor'))` will generate a sub dsm for all the classes with the visitor keywords and their dependents and dependencies.

Sample output of running `dpd@ dsm.subDsm("Mouse.java")` which generates a dsm of all the dependents and dependencies of Mouse.java

```
DPD.DSMDataStructure = [CALL,TYPED,CREATE,IMPLEMENT]
5
0 0 0 0100 0
1111 0 0010 1100 0
0001 0 0 1100 0
0 0100 0100 0 0
0 0100 0100 0001 0
D:\Code\IdeaProjects\DesignPatterns\src\CommonPatterns\visitor\ComputerPart.java
D:\Code\IdeaProjects\DesignPatterns\src\CommonPatterns\visitor\Computer.java
D:\Code\IdeaProjects\DesignPatterns\src\CommonPatterns\visitor\Mouse.java
D:\Code\IdeaProjects\DesignPatterns\src\CommonPatterns\visitor\ComputerPartVisitor.java
D:\Code\IdeaProjects\DesignPatterns\src\CommonPatterns\visitor\ComputerPartDisplayVisitor.java
```

**$**

A handle for utility functions.
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
