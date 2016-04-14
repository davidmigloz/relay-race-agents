package com.davidmiguel.relayrace.agents;

import com.davidmiguel.relayrace.utils.AgentsUtils;
import com.davidmiguel.relayrace.behaviours.InitBehaviour;
import com.davidmiguel.relayrace.behaviours.RunnerBehaviour;

import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.mobility.MobilityOntology;
import jade.util.Logger;

/**
 * Parameters: - isCaptain: true/false - target agent
 */
public class RunnerAgent extends Agent {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = 7941245165294941476L;

	private boolean captain;
	private String targetAgent;

	@Override
	protected void setup() {
		// Get arguments (isCaptain, targetAgent)
		Object[] args = getArguments();
		if (args != null && args.length == 2) {
			captain = ((String) args[0]).equalsIgnoreCase("true");
			targetAgent = (String) args[1];
			logger.info("Runner " + getLocalName() + " (C:" + captain + "). Target: " + targetAgent);
		} else {
			logger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Incorrect number of arguments");
			doDelete();
		}
		// Register agent
		String type = captain ? "RAc" : "RA";
		AgentsUtils.registerAgent(this, type);
		// Register content language and movility ontology
		getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(MobilityOntology.getInstance());
		// Add behaviour
		if(captain){
			addBehaviour(new InitBehaviour());
		} else {
			addBehaviour(new RunnerBehaviour());
		}
	}

	@Override
	protected void afterMove() {
		// Register content language and movility ontology
		getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(MobilityOntology.getInstance());
	}
}