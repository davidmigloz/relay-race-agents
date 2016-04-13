package com.davidmiguel.relayrace.agents;

import com.davidmiguel.relayrace.utils.AgentsUtils;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class JudgeAgent extends Agent {
	
	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = 8537617067799528285L;
	
	/** Delay to start the experiment (in seconds) */
	private static final int DELAY = 10;
	
	private DFAgentDescription[] runners;
	
	@Override
	protected void setup() {
		// Get list of runner agents
		try {
			runners = DFService.search(this, AgentsUtils.getDFD(AgentsUtils.getSD("RA")));
			logger.info("Found " + runners.length + " runners");
		} catch (FIPAException e) {
			logger.log(Logger.SEVERE, "Cannot get runners", e);
		}
		// Send START_TIME message to all runners
		ACLMessage startMsg = new ACLMessage(ACLMessage.REQUEST);
		for (int i = 0; i < runners.length; ++i) {
			startMsg.addReceiver(runners[i].getName());
		}
		long startTime = System.currentTimeMillis() + DELAY * 1000;
		startMsg.setContent(startTime + "");
		this.send(startMsg);
		logger.info("Start time sent!");
	}
}