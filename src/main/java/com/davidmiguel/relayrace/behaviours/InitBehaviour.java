package com.davidmiguel.relayrace.behaviours;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.davidmiguel.relayrace.agents.RunnerAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class InitBehaviour extends SimpleBehaviour {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = -8974966583277442879L;

	private boolean initialized;
	private MessageTemplate mtJudge;
	private Pattern pattern;
	private Date startTime;

	public InitBehaviour() {
		super();
		this.initialized = false;
		this.mtJudge = MessageTemplate.and(MessageTemplate.MatchSender(new AID("JudgeAgent", AID.ISLOCALNAME)),
				MessageTemplate.MatchConversationId("init"));
		this.pattern = Pattern.compile("(\\d+);(\\d+)"); // "startTime;numLaps"
	}

	@Override
	public void action() {
		// Listen to START message from JudgeAgent
		ACLMessage msg = myAgent.receive(mtJudge);
		if (msg != null) {
			Matcher m = pattern.matcher(msg.getContent());
			if (m.find()) {
				// Confirm reception
				ACLMessage confMsg = new ACLMessage(ACLMessage.INFORM);
				confMsg.setConversationId("init");
				confMsg.addReceiver(msg.getSender());
				myAgent.send(confMsg);
				// Get start time
				startTime = new Date(Long.parseLong(m.group(1)));
				// Get number of laps
				int numLaps = Integer.parseInt(m.group(2));
				((RunnerAgent) myAgent).setNumLaps(numLaps);
				logger.info(myAgent.getLocalName() + ": Start msg received: (" + startTime.toString() + ";" + numLaps
						+ ")");
				// Start running at agreed time
				new Timer().schedule(new TimerTask() {
					public void run() {
						logger.info(myAgent.getLocalName() + ":RUUUUUUUUUUUUUUNNN!!!!!");
						Behaviour runnerBehaviour = new RunnerBehaviour(1);
						((RunnerAgent) myAgent).setRunnerBehaviour(runnerBehaviour);
						myAgent.addBehaviour(runnerBehaviour);
					}
				}, startTime);
				initialized = true;
			}
		} else {
			block();
		}
	}

	@Override
	public boolean done() {
		return initialized;
	}
}