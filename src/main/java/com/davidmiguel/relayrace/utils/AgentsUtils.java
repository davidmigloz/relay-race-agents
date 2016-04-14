package com.davidmiguel.relayrace.utils;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
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
}