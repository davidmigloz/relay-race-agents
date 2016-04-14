package com.davidmiguel.relayrace.utils;

import java.util.Iterator;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Location;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.WhereIsAgentAction;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class AgentsUtils {

	private final static Logger logger = Logger.getMyLogger(AgentsUtils.class.getName());

	/**
	 * Register agent in the yellow pages.
	 * 
	 * @param agent
	 *            agent to register
	 * @param type
	 *            service type
	 */
	public static void registerAgent(Agent agent, String type) {
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(type + "Service");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(agent.getAID());
		dfd.addServices(sd);
		try {
			DFService.register(agent, dfd);
		} catch (FIPAException e) {
			logger.log(Logger.SEVERE, "Cannot register agent", e);
		}
	}

	/**
	 * Get new service description.
	 * 
	 * @param type
	 *            type slot
	 * @return ServiceDescription
	 */
	public static ServiceDescription getSD(String type) {
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		return sd;
	}

	/**
	 * Get a DFAgentDescription with the service added.
	 * 
	 * @param service
	 *            service slot
	 * @return DFAgentDescription
	 */
	public static DFAgentDescription getDFD(ServiceDescription service) {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(service);
		return dfd;
	}

	public static ACLMessage prepareRequestToAMS(Agent runner, AID targetAgent) {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.addReceiver(runner.getAMS());
		request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		request.setOntology(MobilityOntology.NAME);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

		// creates the content of the ACLMessage
		Action act = new Action();
		act.setActor(runner.getAMS());

		WhereIsAgentAction action = new WhereIsAgentAction();
		action.setAgentIdentifier(targetAgent);

		act.setAction(action);
		try {
			runner.getContentManager().fillContent(request, act);
		} catch (CodecException ignore) {
		} catch (OntologyException ignore) {
		}
		return request;
	}
	
	@SuppressWarnings("rawtypes")
	public static Location parseAMSResponse(Agent runner, ACLMessage response) {
		Result results = null;
		try {
			results = (Result) runner.getContentManager().extractContent(response);
		} catch (UngroundedException e) {
		} catch (CodecException e) {
		} catch (OntologyException e) {
		}
		Iterator it = results.getItems().iterator();
		Location loc = null;
		if (it.hasNext())
			loc = (Location) it.next();
		return loc;
	}
}