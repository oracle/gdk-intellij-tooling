# Graal Cloud Native IntelliJ IDEA extensions

This suite provides extensions to IntelliJ that supports development of Graal Cloud Native (GCN) and Micronaut applications.

## Build extensions from sources

To build `.zip` file that corresponds to IntelliJ extension format, take the following steps:

- Build [gcn project](https://github.com/oracle/gcn) separately and publish it to local Maven repo with gradle `./gradlew publishToMavenLocal`

- Build extensions with gradle `./gradlew build`

Extensions should be located in `/build/distributions`

## Installation

Extensions can be installed through IntelliJ graphical interface:

- Open settings panel and navigate to Plugins section on the left
- Select `install plugin from disk` option and navigate to the `.zip` file
- Restart the IDE

## Contributing

This project welcomes contributions from the community. Before submitting a pull request, please [review our contribution guide](./CONTRIBUTING.md)

## Security

Please consult the [security guide](./SECURITY.md) for our responsible security vulnerability disclosure process


## License

Copyright (c) 2019, 2024, Oracle and/or its affiliates. All rights reserved.

Released under the [Apache License version 2.0](LICENSE.txt).