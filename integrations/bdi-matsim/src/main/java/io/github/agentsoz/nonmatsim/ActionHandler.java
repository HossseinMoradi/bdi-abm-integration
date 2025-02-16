package io.github.agentsoz.nonmatsim;

/*
 * #%L
 * BDI-ABM Integration Package
 * %%
 * Copyright (C) 2014 - 2025 by its authors. See AUTHORS file.
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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import io.github.agentsoz.bdiabm.data.ActionContent;
import io.github.agentsoz.util.ActionList;

/**
 * @author Edmund Kemsley Processes action/s from MatsimActionList by updating
 *         MatsimAgents and Matsim system
 */

public final class ActionHandler {

	private final Map<String, BDIActionHandler> registeredActions = new LinkedHashMap<>();

	public final void registerBDIAction(String actionID, BDIActionHandler actionHandler) {
		System.out.println("Registering action: " + actionID);
		registeredActions.put(actionID, actionHandler);
	}

	final ActionContent.State processAction(String agentID, String actionID, Object[] parameters) {
		System.out.println("Processing action: " + actionID + " for agent: " + agentID);
		System.out.println("Registered actions: " + registeredActions.keySet());

		if (registeredActions.isEmpty()) {
			throw new RuntimeException("No BDI actions registered; aborting.");
		}

		for (String action : registeredActions.keySet()) {
			if (actionID.equals(action)) {
				System.out.println("Invoking handler for action: " + actionID + " with parameters: " + Arrays.toString(parameters));
				return registeredActions.get(actionID).handle(agentID, actionID, parameters);
			}
		}

		System.out.println("Action not found: " + actionID);
		return ActionContent.State.FAILED;
	}

	public void deregisterBDIAction(String actionID) {
		System.out.println("Deregistering action: " + actionID);
		registeredActions.remove(actionID);
	}
}
