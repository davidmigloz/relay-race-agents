package com.davidmiguel.relayrace.behaviours;

import com.davidmiguel.relayrace.utils.AgentsUtils;

import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class ExperimentBehaviour extends SimpleBehaviour {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = 6100703780980004688L;
	
	/** Delay to start the experiment (in seconds) */
	private static final int DELAY = 10;
	
	private int numTeams;
	private int numMachines;
	private DFAgentDescription[] captains;

	@Override
	public void action() {
		try {
			// Get captains of all teams (RAc)
			captains = DFService.search(myAgent, AgentsUtils.getDFD(AgentsUtils.getSD("RAc")));
			
		} catch (FIPAException e) {
			logger.log(Logger.SEVERE, "Cannot get runners", e);
		}
		// Send START_TIME message to all runners
		ACLMessage startMsg = new ACLMessage(ACLMessage.REQUEST);
		for (int i = 0; i < captains.length; ++i) {
			startMsg.addReceiver(captains[i].getName());
		}
		long startTime = System.currentTimeMillis() + DELAY * 1000;
		startMsg.setContent(startTime + "");
		myAgent.send(startMsg);
		logger.info("Start time sent!");
	}

	@Override
	public boolean done() {
		return false;
	}
}