#include "inc/inc.h"
#include "testinclude/testinclude.h"
#include <iostream>

int main() {
  std::cout << inner(1) << std::endl;
  std::cout << testinclude() << std::endl;
  return 0;
}