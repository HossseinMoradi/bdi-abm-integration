package io.github.agentsoz.conservation.jill.plans;

/*
 * #%L
 * BDI-ABM Integration Package
 * %%
 * Copyright (C) 2014 - 2015 by its authors. See AUTHORS file.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import io.github.agentsoz.conservation.ConservationUtils;
import io.github.agentsoz.conservation.Log;
import io.github.agentsoz.conservation.jill.agents.Landholder;
import io.github.agentsoz.conservation.outputwriters.AgentsProgressWriter;
import io.github.agentsoz.jill.lang.Agent;
import io.github.agentsoz.jill.lang.Goal;
import io.github.agentsoz.jill.lang.Plan;
import io.github.agentsoz.jill.lang.PlanStep;

import java.util.HashMap;

/**
 * This plan is executed for land owners who have High C and Low P. The land
 * owners who come here have high probability of participating in auction. The
 * probability is defined in ConservationUtils.highParticipationProbability.
 * 
 * @author Sewwandi Perera
 */
public class DecideParticipationWhenHighCLowPPlan extends Plan {
	Landholder landholder;

	public DecideParticipationWhenHighCLowPPlan(Agent agent, Goal goal,
			String name) {
		super(agent, goal, name);
		landholder = (Landholder) getAgent();
		body = steps;
	}

	@Override
	public boolean context() {
		return ((Landholder) getAgent()).isConservationEthicHigh()
				&& !((Landholder) getAgent()).isProfitMotivationHigh();
	}

	@Override
	public void setPlanVariables(HashMap<String, Object> vars) {
	}

	PlanStep[] steps = { new PlanStep() {
		public void step() {
			Log.debug("Agent " + landholder.getName()
					+ " Probability of agent " + landholder.getName()
					+ " participate in auction is "
					+ ConservationUtils.getHighParticipationProbability());

			if (ConservationUtils.getGlobalRandom().nextDouble() < ConservationUtils
					.getHighParticipationProbability()) {
				Log.debug("Agent " + landholder.getName()
						+ " decided to participate in Auction");
				landholder.setDecisionOnParticipation(true);
			} else {
				Log.debug("Agent " + landholder.getName()
						+ " decided *not* to participate in Auction");
				landholder.setDecisionOnParticipation(false);
				AgentsProgressWriter.getInstance().addAgentsInfo(
						landholder.getName(), "B-np");
			}
		}
	} };
}
