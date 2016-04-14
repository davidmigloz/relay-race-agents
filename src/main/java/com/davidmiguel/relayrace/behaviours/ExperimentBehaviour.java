package com.davidmiguel.relayrace.behaviours;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.davidmiguel.relayrace.utils.AgentsUtils;

import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class ExperimentBehaviour extends SimpleBehaviour {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = 6100703780980004688L;

	/** Delay to start the experiment (in seconds) */
	private static final int DELAY = 10;

	private JSONArray results;
	private int numTeams;
	private int numMachines;
	private DFAgentDescription[] captains;
	private long startTime;
	private long endTime;
	private int numLaps;
	private int attempt;
	private int numMaxAttempts;
	private int step;
	private MessageTemplate iMT;
	private MessageTemplate cMT;
	private int teamsConfirmed;

	public ExperimentBehaviour(int numAttempts, int initLaps, int step) {
		this.results = new JSONArray();
		this.attempt = 1;
		this.numLaps = initLaps;
		this.numMaxAttempts = numAttempts;
		this.step = step;
		this.iMT = MessageTemplate.MatchConversationId("init");
		this.cMT = MessageTemplate.MatchConversationId("completion");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		try {
			// Get captains of all teams (RAc)
			captains = DFService.search(myAgent, AgentsUtils.getDFD(AgentsUtils.getSD("RAc")));
			// Count number of teams
			numTeams = captains.length;
			// Get rest of members of the teams (RA)
			DFAgentDescription[] runners = DFService.search(myAgent, AgentsUtils.getDFD(AgentsUtils.getSD("RA")));
			// Count number of machines (one member per machine)
			numMachines = runners.length / numTeams;
		} catch (FIPAException e) {
			logger.log(Logger.SEVERE, "Cannot get runners", e);
		}
		// Send start time and number of laps to team captains
		ACLMessage startMsg = new ACLMessage(ACLMessage.REQUEST);
		startMsg.setConversationId("init");
		for (int i = 0; i < captains.length; ++i) {
			startMsg.addReceiver(captains[i].getName());
		}
		startTime = System.currentTimeMillis() + DELAY * 1000;
		startMsg.setContent(startTime + ";" + numLaps);
		myAgent.send(startMsg);
		logger.info("Start msg sent!");
		// Receive confirmations
		teamsConfirmed = 0;
		while (teamsConfirmed != numTeams) {
			ACLMessage confMsg = this.getAgent().receive(iMT);
			if (confMsg != null) {
				teamsConfirmed++;
				logger.info("Teams confirmed: " + teamsConfirmed + "/" + numTeams);
			}
		}
		// Receive completion confirmations
		teamsConfirmed = 0;
		while (teamsConfirmed != numTeams) {
			ACLMessage compMsg = this.getAgent().receive(cMT);
			if (compMsg != null) {
				teamsConfirmed++;
				logger.info("Teams finished: " + teamsConfirmed + "/" + numTeams);
			}
		}
		// Get end time
		endTime = System.currentTimeMillis();
		// Save results
		JSONObject attResults = new JSONObject();
		attResults.put("machines", numMachines);
		attResults.put("teams", numTeams);
		attResults.put("laps", numLaps);
		attResults.put("runtime", endTime - startTime);
		results.add(attResults);
		// Update variables
		numLaps += step;
		attempt++;
	}

	@Override
	public boolean done() {
		return attempt > numMaxAttempts;
	}

	@Override
	public int onEnd() {
		// Save results to file
		try {
			File file = new File("result.json");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			fileWritter.write(results.toJSONString());
			fileWritter.flush();
			fileWritter.close();
		} catch (IOException e) {
			logger.log(Logger.SEVERE, "Cannot write results to file");
		}
		return 0;
	}
}