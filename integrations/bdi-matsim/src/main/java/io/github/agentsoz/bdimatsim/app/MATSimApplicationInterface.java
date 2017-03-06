package io.github.agentsoz.bdimatsim.app;

/*
 * #%L
 * BDI-ABM Integration Package
 * %%
 * Copyright (C) 2014 - 2017 by its authors. See AUTHORS file.
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

import java.util.List;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.mobsim.qsim.ActivityEndRescheduler;
import io.github.agentsoz.bdimatsim.MATSimActionHandler;
import io.github.agentsoz.bdimatsim.MatsimPerceptHandler;
import io.github.agentsoz.bdimatsim.Replanner;

/**
 * Convinience interface for use by applications; provides initialisaiton hooks,
 * abilitiy to register custom BDI actions/percepts.
 * @author dsingh
 *
 */
public interface MATSimApplicationInterface {

	/**
	 * Called by MATSim to allow the application to register any custom
	 * BDI actions, using 
	 * {@link MATSimActionHandler#registerBDIAction(String, BDIActionHandler)}.
	 * @param withHandler
	 */
	public void registerNewBDIActions(MATSimActionHandler withHandler);
	
	/**
	 * Called by MATSim to allow the application to register any custom
	 * BDI percepts, using 
	 * {@link MatsimPerceptHandler#registerBDIPercepts(String, BDIPerceptHandler)}.
	 * @param withHandler
	 */
	public void registerNewBDIPercepts(MatsimPerceptHandler withHandler);
	
	/**
	 * Called by MATSim prior to BDI agents being created and registered;
	 * allows the BDI agents list to be adjusted if required 
	 * @param model
	 */
	public void notifyBeforeCreatingBDICounterparts(List<Id<Person>>bdiAgentsIds);
	
	/**
	 * Called by MATSim after all the BDI agents have been created and added 
	 * to MATSim; allows created BDI agents to be individually initialised
	 * with application specific data, prior to the first simulation step 
	 * @param bdiAgents map of all BDI agents in MATSim
	 */
	public void notifyAfterCreatingBDICounterparts(List<Id<Person>> bdiAgentsIDs);

	
	/** 
	 * Allows the app to use a custom {@link Replanner}
	 * <p>
	 * <i>TODO: This is not ideal. Here we just allow the default Replanner 
	 * to be extended by the application. Instead it should be an interface
	 * (Dhi Mar17) </i>
	 * @param qSim 
	 * @return
	 */
	public Replanner getReplanner(ActivityEndRescheduler activityEndRescheduler);

	
	
}
