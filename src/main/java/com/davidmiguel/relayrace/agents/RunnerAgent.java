package com.davidmiguel.relayrace.agents;

import com.davidmiguel.relayrace.utils.AgentsUtils;
import com.davidmiguel.relayrace.behaviours.InitBehaviour;
import com.davidmiguel.relayrace.behaviours.RunnerBehaviour;

import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

/**
 * Parameters: - isCaptain: true/false - target agent
 */
public class RunnerAgent extends Agent {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = 7941245165294941476L;

	private boolean captain;
	private AID targetAgent;
	private String originLocation;
	private int numLaps;
	private int completedLaps;

	@Override
	protected void setup() {
		// Get arguments (isCaptain, targetAgent)
		Object[] args = getArguments();
		if (args != null && args.length == 2) {
			captain = ((String) args[0]).equalsIgnoreCase("true");
			targetAgent = new AID((String) args[1], AID.ISLOCALNAME);
			logger.info("Runner " + getLocalName() + " (C:" + captain + "). Target: " + targetAgent);
		} else {
			logger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Incorrect number of arguments");
			doDelete();
		}
		// Register agent
		String type = captain ? "RAc" : "RA";
		AgentsUtils.registerAgent(this, type);
		// Save origin location
		originLocation = getContainerController().getName();
		// Init laps counter
		completedLaps = 0;
		// Register content language and movility ontology
		getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(MobilityOntology.getInstance());
		// Add behaviour
		if (captain) {
			addBehaviour(new InitBehaviour(this));
		} else {
			addBehaviour(new RunnerBehaviour(this, 0));
		}
	}

	@Override
	protected void afterMove() {
		if (captain) {
			// Get new location
			String newLocation = getContainerController().getName();
			logger.info("Reach new location: " + newLocation);
			if (newLocation.equals(originLocation)) {
				// If is the origin -> one lap completed
				completedLaps++;
				logger.info("New lap completed!. " + completedLaps + "/" + numLaps);
				if (completedLaps >= numLaps) {
					// All laps completed
					logger.info("All laps completed!!!");
					// Send competition message to judge and finish
					ACLMessage compMsg = new ACLMessage(ACLMessage.INFORM);
					compMsg.setConversationId("completion");
					compMsg.addReceiver(new AID("JudgeAgent", AID.ISLOCALNAME));
					send(compMsg);
					return;
				}
			}
		}
		// Register content language and movility ontology
		getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(MobilityOntology.getInstance());
		// Send message to local agent to start running
		ACLMessage runMsg = new ACLMessage(ACLMessage.REQUEST);
		runMsg.setConversationId("running");
		runMsg.addReceiver(targetAgent);
		send(runMsg);
		// Wait its anwer
		MessageTemplate mtRunner = MessageTemplate.MatchConversationId("running");
		ACLMessage msg;
		do {
			msg = receive(mtRunner);
		} while (msg == null);
	}

	public AID getTargetAgent() {
		return this.targetAgent;
	}

	public void setNumLaps(int num) {
		this.numLaps = num;
	}
}