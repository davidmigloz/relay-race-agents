#Relay race

Test mobility in the JADE agents platform implementing a "realay race".

## JudgeAgent

- Receives three parameters:
   1. `numAttempts`: total number of attempts to run.  Ej: `1` *(Atm it just works with 1)*
   2. `initLaps`: number of laps in first attemp. Ej: `10`
   3. `step:` number of laps to increase in each attempt. Ej: `5`
 
#### Notes:
 
 1. It must be named: `JudgeAgent`
 
### ExperimentBehaviour:
 
#### Pseudocode
 
 - Setup:
   1. Get captains of all teams (RAc).
   2. Count number of teams.
   3. Get rest of members of the teams (RA).
   4. Count number of machines (one member per machine).
 - Init experiment:
   1. Send start time and number of laps to team captains.
   2. Receive confirmations.
 - Run experiment:
   1. Receive completion confirmations.
   2. Stop timer when all confirmations are received.
   3. Save results.
   4. Update variables and behaviours for next attempt.
 - Finish experiment:
   1. Save results to file.
 
#### Notes:
 
1. Default delay: 10seg. 
2. Structure of start message: `startTime;numLaps`
    - `startTime`: the difference, measured in milliseconds, 
                 between the start time and midnight, January 1, 1970 UTC (Unix time).
    - `numLaps`: number of laps to run.

## RunnerAgent

- Receives two parameters:
  1. `isCaptain`: true/false - if the agent is the captain of the team. Ej: `false`
  2. `targetAgent`: name of the agent that it has to reach. Ej: `a2`

#### Pseudocode
 
- Setup:
  1. Get parameters.
  2. Register agent in yellow pages.
  3. Save origin location and init laps counter to 0.
  4. Register content language and movility ontology.
    - If it is captain: Add `InitBehaviour`.
    - If not: Add `RunnerBehaviour` with `step=0`.

### InitBehaviour:

#### Pseudocode
 
  1. Receive start time and number of laps from `JudgeAgent`.
  2. Confirm reception.
  3. Schedule start time.
  4. Start running at agreed time.
  
### RunnerBehaviour:

- Receives one parameter:
  1. `step`: initial action to perform:
    a. `step=0`: wait for message from previous runner.
    b. `setp=1`: run to location of target agent.


#### Pseudocode

 - Wait previous runner:
   1. Receive message from previous runner.
   2. Confirm message.
   3. Start running.
 - Running:
   1. Ask the location of `targetAgent`.
   2. Get reply from AMS with the location.
   3. Run to the destination.
 - AfterMove (in new location):
   + If it is team captain:
      1. Check new location.
      2. If it is the origin -> increase laps counter.
      3. If all laps completed: 
     	   - Send completation message to judge.
     	   - Restart behaviours captain (delete runningBehaviour, add initBehaviour) and variables.
     	   - Exit.
   + All:
      1. Send message to local agent to start running.
      2. Receive confirmation.

--------------------

## Example: 3 teams / 3 machines / 5 laps

### Environment

- Machine 0:
  + JudgeAgent
- Machine 1:
  + A0
  + A1 (c)
  + B3
  + C2
- Machine 2:
  + A2
  + B0
  + B1 (c)
  + C3
- Machine 3:
  + A3
  + B2
  + C0
  + C1 (c)

### Commands:

1º Run plattform:

```bash
java jade.Boot -gui
```

2º Run agents of machine 1:

```bash
java jade.Boot -container 
a0:com.davidmiguel.relayrace.agents.RunnerAgent(false,a1);
a1:com.davidmiguel.relayrace.agents.RunnerAgent(true,a2);
b3:com.davidmiguel.relayrace.agents.RunnerAgent(false,b0);
c2:com.davidmiguel.relayrace.agents.RunnerAgent(false,c3)
```

**No line break between* `;` 

3º Run agents of machine 2:

```bash
java jade.Boot -container 
a2:com.davidmiguel.relayrace.agents.RunnerAgent(false,a3);
b0:com.davidmiguel.relayrace.agents.RunnerAgent(false,b1);
b1:com.davidmiguel.relayrace.agents.RunnerAgent(true,b2);
c3:com.davidmiguel.relayrace.agents.RunnerAgent(false,c0)
```

4º Run agents of machine 3:

```bash
java jade.Boot -container 
a3:com.davidmiguel.relayrace.agents.RunnerAgent(false,a0);
b2:com.davidmiguel.relayrace.agents.RunnerAgent(false,b3);
c0:com.davidmiguel.relayrace.agents.RunnerAgent(false,c1);
c1:com.davidmiguel.relayrace.agents.RunnerAgent(true,c2)
```

4º Run JudgeAgent in machine 0 (the experiment will start inmediatelly):

```bash
java jade.Boot -container JudgeAgent:com.davidmiguel.relayrace.agents.JudgeAgent(1,5,0)
```

**The `-host` has been omitted*
