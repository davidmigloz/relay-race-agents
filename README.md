# Relay Race

Test mobility in the JADE agents platform implementing a "realay race".

## Introduction

Mobile agents are those agents that can migrate between networked systems in an effort to carry out a given task. This is a report on experiments conducted to test the performance of the agent mobility features of the JADE agent platform. The experiments were in the form of a simulated relay race between teams of mobile agents.

## Experiment Design

### Scenario

![Sample scenario with 3 teams](https://raw.githubusercontent.com/davidmigloz/relay-race-agents/master/docs/report/images/scenario.png)

The simulation is made up of teams of agents racing between containers located on different machines. To begin the simulation, a runner agent from each team is placed on each machine. In addition to this, a captain runner agent from each team is placed on each successive machine, so as to alternate the machines on which they begin the race.

To start the race, judge agent sends a specific time to team captains. When it is the time, the captains moves from its container to the container of the next runner agent on its team. Upon arriving at the next container, the runner agent sends a message to its teammate on the container instructing it to move on to the next container. The previous runner agent remains at its new location. When a given number of laps around the network is completed, the team signals to a judge agent that it has completed the race. When all teams have completed the race, the judge agent records the total time taken for all teams to complete the race.

The test parameters are varied as follows:

- Increasing the number of machines gradually from 3 to 7.
- Varying the number of teams from 3 to 21.
- Varying the number of laps from 1 to 15.

### Machine Configuration

The experiment was carried out on multiple Virtual Private Servers across the world from Linode VPS provider. 

The machines were deployed with the following configuration:

- Compute: 1 CPU core
- Memory: 1024MB
- Storage: 24GB SSD
- Operating System: Ubuntu 14.04 LTS
- Linux Kernel Latest 64 bit (4.5.0-x861-64-linode65)
- Oracle Java 1.8
- JADE 4.4.0

## Results

The results obtained from the experiment are shown below.

### Number of Machines vs Runtime

The number of machines was incresed gradually from 3 to 7 (without counting judge agent's machine), with a fixed number of 3 teams and 10 laps.

### Number of Teams vs Runtime

The number of teams was increased gradually from 3 to 21, with a fixed number of 3 machines (without counting judge agent's machine) and 10 laps.

### Number of Laps vs Runtime

The number of laps was increased gradually from 1 to 15, with a fixed number of 3 teams and 3 machines (without counting judge agent's machine).

## Conclusion

We can observe that, with small variation probably caused by network, there is a general trend of linearity in all the experiments, even with 21 teams of agents. We could then conclude that the performance of the mobility feature of the JADE agent platform scales in a linear manner. This should, therefore, be taken into consideration when designing systems, with JADE, that will employ agent mobility.
