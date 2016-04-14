package com.davidmiguel.relayrace.agents;

import com.davidmiguel.relayrace.behaviours.ExperimentBehaviour;

import jade.core.Agent;

public class JudgeAgent extends Agent {

	private static final long serialVersionUID = 934303410329286008L;

	@Override
	protected void setup() {		
		// Init experiment
		addBehaviour(new ExperimentBehaviour());
	}
}