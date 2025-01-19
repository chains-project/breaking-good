# Breaking-good

[![Build](https://github.com/frankreyesgarcia/breaking-good/actions/workflows/maven.yml/badge.svg)](https://github.com/frankreyesgarcia/breaking-good/actions/workflows/maven.yml)
# Overview

Breaking-Good is a tool that provides explanations for breaking dependency updates. It uses build analysis to identify the root cause of the breaking changes and provides a detailed explanation to help developers understand and fix the problem.

Paper: [Proceedings of IEEE International Conference on Source Code Analysis and Manipulation](http://arxiv.org/pdf/2407.03880)

if you use Breaking-Good, please cite:
```bibtex
@inproceedings{reyes2024breaking,
  title={Breaking-Good: Explaining Breaking Dependency Updates with Build Analysis},
  author={Reyes, Frank and Baudry, Benoit and Monperrus, Martin},
  booktitle={Proceedings of IEEE International Conference on Source Code Analysis and Manipulation},
  year={2024},
  doi = {10.1109/scam63643.2024.00014}
}
```

# How to use Breaking-Good

To use Breaking-Good, you need to provide the following inputs:
- The previous version of the dependency
- The new version of the dependency
- **(Optional)** The logs generated in the build process

You can build this tool locally using `mvn package` with Java 17.
You can then run the tool and print usage information with the command:
```bash
java -jar target/Explaining.jar explaining --help 
```

## License

Distributed under the MIT License. See [LICENSE](https://github.com/chains-project/breaking-good/blob/main/LICENSE) for more information.

    


