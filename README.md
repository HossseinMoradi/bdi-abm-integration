# BDI-ABM Integration Package

This software realises a mechanism for interating 
Belief-Desire-Intention (BDI) reasoning into agents within an
agent-based simulation (ABM). The concept is described
in the following papers papers:

1. Dhirendra Singh, Lin Padgham, Brian Logan. 
   Integrating BDI agents with Agent Based Simulation Platforms.
   Autonomous Agents and Multi-agent Systems, 2015. 
   *Under review.*

2. Lin Padgham, Kai Nagel, Dhirendra Singh, Qingyu Chen.
   Integrating BDI Agents into a MATSim Simulation. 
   Frontiers in Artificial Intelligence and Applications 263 (ECAI 2014), 
   pages 681-686, 2014.

3. Lin Padgham, David Scerri, Gaya Buddhinath Jayatilleke, and Sarah Hickmott. 
   Integrating BDI reasoning into agent based modelling and simulation. 
   In Winter Simulation Conference (WSC), pages 345-356, 2011.


## How to use this software

Examples of BDI-ABM applications are provided in the `./examples` directory.

Any BDI-ABM application consists of three layers. A generic first layer
(`./integrations/bdi-abm`) manages the high level interaction and message
passing between the BDI and the ABM system. A second platform specific
layer realises the connection between a specific BDI platform (such as 
JACK, i.e., `./integrations/abm-jack`), and a specific ABM system (such 
as MATSim, i.e., `./integrations/bdi-matsim`). Finally, a third application
layer puts these together along with domain specific code (for instance
`./examples/bushfire). 

Overall, the repository consists of *integrations* and *examples*. Integrations
are platform specific and live in `./integrations`. Examples are domain 
specific, and live in `./examples`. The following integrations
are provided:
 
Integration   | Directory                   | Description
:-------------|:----------------------------|:----------------------------
BDI-ABM       | `./integrations/bdi-abm`    | BDI-ABM communication and data layer
BDI-GAMS      | `./integrations/bdi-gams`   | Integration for GAMS (www.gams.com) 
BDI-MATSim    | `./integrations/bdi-matsim` | Integration for MATSim (www.matsim.org)
BDI-Repast    | `./integrations/bdi-repast` | Integration for Repast (repast.sourceforge.net)
ABM-GORITE    | `./integrations/abm-gorite` | Integration for GORITE (www.intendico.com/gorite)
ABM-JACK      | `./integrations/abm-jack`   | Integration for JACK (aosgrp.com/products/jack)
ABM-Jadex     | `./integrations/abm-jadex`  | Integration for Jadex (http://www.activecomponents.org/bin/view/About/Features)

Integrations are pulled together to build application examples. The following
examples are provided:

Example             | Directory                  | Description
:-------------------|:---------------------------|:----------------------------
Bushfire            | `./examples/bushfire`      | Uses JACK and MATSim
Conservation Ethics | `./examples/cnoservation`  | Uses JACK and GAMS
Taxi Service        | `./examples/taxi`          | Uses GORITE and MATSim
Child Vaccination   | `./examples/vaccination`   | Uses JACK and a custom Python-based ABM
Humans and Zombies  | `./examples/zombies`       | Uses Jadex and Repast



<a name="Dependencies"></a>
## Build Dependencies 


* Java Development Kit 1.7 
  http://en.wikipedia.org/wiki/Java_Development_Kit

* ECLIPSE Luna with built-in Maven support
  https://www.eclipse.org

* Apache Maven 3.3.1
  maven.apache.org

* Some of the integrations (e.g., JACK, GAMS) require third-party 
  libraries to be installed in your local Maven repository. See 
  the respective READMEs (`./integrations/*/README.md`) for details.
  *The project will not build unless these dependencies have been 
   resolved.*

## Compiling

This software is managed by the Apache Maven software management tool. You
can build the software in two ways. 

* The entire suite of integrations and examples can be built using the 
  command line. Ensure that you have Maven installed
  (see [Dependencies](#Dependencies)). Then from the top level directory
  containing `pom.xml`, do `mvn clean install`.

* Each integration and example also contains an Eclipse project. First 
  ensure that you have an appropriate version that supports Maven 
  (see [Dependencies](#Dependencies)). Then import the existing project 
  (e.g., `./integrations/bdi-abm/.project`) into Eclipse, and it should 
  build without any additional configuration. 



## License


BDI-ABM Integration Package
Copyright (C) 2014, 2015 by its authors. See AUTHORS file.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

For contact information, see AUTHORS file.


