# Graphoenix

Open source and community-based implementation of Nx Cloud.

[![License](https://img.shields.io/github/license/clementguillot/graphoenix)]()
[![build](https://github.com/clementguillot/graphoenix/actions/workflows/ci.yaml/badge.svg)](https://github.com/clementguillot/graphoenix/actions/workflows/ci.yaml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=clementguillot_graphoenix&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=clementguillot_graphoenix)
[![Renovate enabled](https://img.shields.io/badge/renovate-enabled-brightgreen.svg)](https://renovatebot.com/)

## What is Nx Cloud?

[Nx Cloud](https://nx.app) is a great suite of cloud-powered capabilities designed to enhance the development experience for teams using the [Nx](https://github.com/nrwl/nx) to manage their monorepo projects.

## Why Graphoenix?

Since Nx Cloud is a proprietary software from Narwhal Technologies Inc., it is hard, almost impossible to actively contribute to its development. Graphoenix aims to open to everyone the possibility to bring new features.

## Features

- **Remote Caching** leverages distributed caching to store and share results of development tasks (e.g., builds, tests, and linting) across the team. This means that once a task is executed by one team member, others can reuse the cached result, drastically reducing the time required for these tasks.

## Project structure

| Application/library                  | Summary                                                      |
| ------------------------------------ | ------------------------------------------------------------ |
| [`apps/console`](apps/console)       | Frontend console (web UI) of Graphoenix.                     |
| [`apps/helm-chart`](apps/helm-chart) | Helm Chart to deploy Graphoenix on Kubernetes                |
| [`apps/server`](apps/server)         | Backend of Graphoenix, handles requests from Nx Cloud client |

## External Code and Licenses

This project includes external code from `nx-cloud-client-bundle`, developed by Victor Savkin (Narwhal Technologies Inc.). This client is downloaded and installed by Nx when Nx Cloud is activated in the repository. The Nx Cloud Client is in charge of communications with Nx Cloud Server, as well as uploading artifacts etc.

### Licensing of External Code

The external code is licensed under Creative Commons Attribution-NoDerivs 3.0 Unported (CC-BY-ND-3.0), which requires that users give credit to the original author in any derivative works but does not allow for the distribution of modified versions of the work.

#### Attribution

This project uses `nx-cloud-client-bundle` by Victor Savkin (Narwhal Technologies Inc.), available at [https://cloud.nx.app/nx-cloud/static/client-bundle](https://cloud.nx.app/nx-cloud/static/client-bundle). This code is licensed under CC-BY-ND-3.0 ([https://creativecommons.org/licenses/by-nd/3.0/](https://creativecommons.org/licenses/by-nd/3.0/)). We have complied with the license requirements by including this unmodified code in our project and providing the above attribution.

#### Usage Guidelines for Our Users

While our project's source code is licensed under MIT license, please note that the `nx-cloud-client-bundle` component retains its original CC-BY-ND-3.0 licensing. It means that while you can freely use, modify, and distribute our code, the `nx-cloud-client-bundle` component must remain unchanged and be attributed according to the terms of its license.

For further details on the license and restrictions, please visit [CC BY-ND 3.0 DEED](https://creativecommons.org/licenses/by-nd/3.0/).
