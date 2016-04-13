package com.davidmiguel.relayrace.agents;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.davidmiguel.relayrace.utils.AgentsUtils;

import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPANames;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class RunnerAgent extends Agent {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = 7941245165294941476L;

	@Override
	protected void setup() {
		// Register agent
		AgentsUtils.registerAgent(this, "RA");
		// Register content language and movility ontology
		getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(MobilityOntology.getInstance());
		// Add behaviour
		addBehaviour(new RelayRaceBehaviour());
	}

	@Override
	protected void afterMove() {
		// Register content language and movility ontology
		getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(MobilityOntology.getInstance());
	}

	private class RelayRaceBehaviour extends SimpleBehaviour {

		private static final long serialVersionUID = -8974966583277442879L;
		private int step = 0;
		private MessageTemplate mtJudge;
		private Date startTime;
		private Timer timer;

		public RelayRaceBehaviour() {
			super();
			mtJudge = MessageTemplate.MatchSender(new AID("JudgeAgent", AID.ISLOCALNAME));
		}

		@Override
		public void action() {
			switch (step) {
			case 0:
				// Listen to START message from JudgeAgent
				ACLMessage msg = myAgent.receive(mtJudge);
				if (msg != null) {
					// Get start time
					startTime = new Date(Long.parseLong(msg.getContent()));
					logger.info("Start time received: " + startTime.toString());
					step++;
				} else {
					block();
				}
				break;
			case 1:
				// Start running at given time
				timer = new Timer();
				timer.schedule(new TimerTask() {
					public void run() {
						logger.info("RUUUUUUUUUUUUUUNNN!!!!!");
					}
				}, startTime);
				step++;
				break;
			}
		}

		@Override
		public boolean done() {
			return step == 2;
		}
	}
}