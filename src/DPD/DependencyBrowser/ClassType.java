package DPD.DependencyBrowser;

/**
 * Created by Justice on 1/27/2016.
 */
public
enum ClassType { // move out from this module to enums
    Interface,
    Class,
    Abstract,
    Enum,
    Final,
    Partial,
    Abstraction, // true for Abstract or Interfaces
    Concrete, // true for Class or Final
}
