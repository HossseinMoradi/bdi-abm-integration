package io.github.agentsoz.ees.agents.bushfire;


/*
 * #%L
 * Jill Cognitive Agents Platform
 * %%
 * Copyright (C) 2014 - 2016 by its authors. See AUTHORS file.
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

import io.github.agentsoz.bdiabm.QueryPerceptInterface;
import io.github.agentsoz.bdiabm.data.ActionContent;
import io.github.agentsoz.jill.core.beliefbase.Belief;
import io.github.agentsoz.jill.core.beliefbase.BeliefBaseException;
import io.github.agentsoz.jill.core.beliefbase.BeliefSetField;
import io.github.agentsoz.jill.lang.Agent;
import io.github.agentsoz.jill.lang.AgentInfo;
import io.github.agentsoz.util.EmergencyMessage;
import io.github.agentsoz.util.Location;
import io.github.agentsoz.util.evac.ActionList;
import io.github.agentsoz.util.evac.PerceptList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

@AgentInfo(hasGoals={"io.github.agentsoz.ees.agents.bushfire.GoalDoNothing"})
public abstract class BushfireAgent extends  Agent implements io.github.agentsoz.bdiabm.Agent {

    private final Logger logger = LoggerFactory.getLogger("io.github.agentsoz.ees");
    PrintStream writer = null;
    private QueryPerceptInterface queryInterface;
    private double time = -1;
    private BushfireAgent.Prefix prefix = new BushfireAgent.Prefix();

    // Defaults
    private boolean hasDependents = false;
    private double initialResponseThreshold = 0.5;
    private double finalResponseThreshold = 0.5;
    private double responseBarometerMessages = 0.0;
    private double responseBarometerFieldOfView = 0.0;

    private enum FieldOfViewPercept {
        SMOKE_VISUAL(0.3),
        FIRE_VISUAL(0.4),
        NEIGHBOURS_LEAVING(0.5);

        private final double value;

        private FieldOfViewPercept(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }
    }

    enum MemoryEventType {
        RESPONSE_BAROMETER_MESSAGES_CHANGED,
        RESPONSE_BAROMETER_FIELD_OF_VIEW_CHANGED,
        PERCEIVED,
        DECIDED,
        ACTIONED
    }

    enum MemoryEventValue {
        INITIAL_RESPONSE_THRESHOLD_BREACHED,
        FINAL_RESPONSE_THRESHOLD_BREACHED,
        VISIT_DEPENDENT
    }

    // Internal variables
    private final String memory = "memory";

    public BushfireAgent(String id) {
        super(id);
    }

    boolean isHasDependents() {
        return hasDependents;
    }

    double getResponseBarometer() {
        return responseBarometerMessages + responseBarometerFieldOfView;
    }


    /**
     * Called by the Jill model when starting a new agent.
     * There is no separate initialisation call prior to this, so all
     * agent initialisation should be done here (using params).
     */
    @Override
    public void start(PrintStream writer, String[] params) {
        this.writer = writer;
        parseArgs(params);
        // Create a new belief set to store memory
        BeliefSetField[] fields = {
                new BeliefSetField("time", String.class, true),
                new BeliefSetField("event", String.class, false),
                new BeliefSetField("value", String.class, false),
        };
        try {
            // Attach this belief set to this agent
            this.createBeliefSet(memory, fields);
        } catch (BeliefBaseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called by the Jill model when terminating
     */
    @Override
    public void finish() {
        writer.println(logPrefix() + "is terminating");
        try {
            if (eval("memory.event = *")) {
                Set<String> memories = new TreeSet<>();
                for (Belief belief : getLastResults()) {
                    memories.add(logPrefix() + "memory : " + Arrays.toString(belief.getTuple()));
                }
                for (String belief : memories) {
                    writer.println(belief);
                }
            }
        } catch (BeliefBaseException e) {
            throw new RuntimeException(e);
        }
    }

        /**
     * Called by the Jill model with the status of a BDI percept
     * for this agent, coming from the ABM environment.
     */
    @Override
    public void handlePercept(String perceptID, Object parameters) {
        if (perceptID == null || perceptID.isEmpty()) {
            return;
        }
        if (perceptID.equals(PerceptList.TIME)) {
            if (parameters instanceof Double) {
                time = (double) parameters;
            }
            return;
        }

        writer.println(logPrefix() + "received percept " + perceptID +  ":" + parameters);
        // save it to memory
        memorise(MemoryEventType.PERCEIVED.name(), perceptID + ":" +parameters.toString());

        if (perceptID.equals(PerceptList.EMERGENCY_MESSAGE)) {
            updateResponseBarometerMessages(parameters);
        } else if (perceptID.equals(PerceptList.FIELD_OF_VIEW)) {
            updateResponseBarometerFieldOfViewPercept(parameters);
        } else if (perceptID.equals(PerceptList.ARRIVED)) {
            // do something
        } else if (perceptID.equals(PerceptList.BLOCKED)) {
            // do something
        } else if (perceptID.equals(PerceptList.FIRE_ALERT)) {
            // FIXME: using fire msg (global) as proxy for fire visual (localised)
            updateResponseBarometerFieldOfViewPercept(FieldOfViewPercept.FIRE_VISUAL);
        }

        // Now trigger a response as needed
        checkBarometersAndTriggerResponseAsNeeded();
    }

    protected void checkBarometersAndTriggerResponseAsNeeded() {
        try {
            if (!eval("memory.value = " + MemoryEventValue.INITIAL_RESPONSE_THRESHOLD_BREACHED.name())) {
                // initial response threshold not breached yet
                if (getResponseBarometer() >= initialResponseThreshold) {
                    // initial response threshold breached for the first time
                    memorise(MemoryEventType.DECIDED.name(), MemoryEventValue.INITIAL_RESPONSE_THRESHOLD_BREACHED.name());
                    triggerResponse(MemoryEventValue.INITIAL_RESPONSE_THRESHOLD_BREACHED);
                }
            }
            if (!eval("memory.value = " + MemoryEventValue.FINAL_RESPONSE_THRESHOLD_BREACHED.name())) {
                // final response threshold not breached yet
                if (getResponseBarometer() >= finalResponseThreshold) {
                    // final response threshold breached for the first time
                    memorise(MemoryEventType.DECIDED.name(), MemoryEventValue.FINAL_RESPONSE_THRESHOLD_BREACHED.name());
                    triggerResponse(MemoryEventValue.FINAL_RESPONSE_THRESHOLD_BREACHED);
                }
            }
        } catch (BeliefBaseException e) {
            throw new RuntimeException(e);
        }
    }

    private void memorise(String event, String data) {
        try {
            addBelief(memory, Double.toString(time), event, data);
        } catch (BeliefBaseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called after a new percept has been processed
     * @param breach
     */
    abstract void triggerResponse(MemoryEventValue breach);

    /**
     * Overwrites {@link #responseBarometerFieldOfView} with the value of the incoming visual
     * if the incoming value is higher.
     * @param view the incoming visual percept
     */
    private void updateResponseBarometerFieldOfViewPercept(Object view) {
        if (view == null || !(view instanceof  FieldOfViewPercept)) {
            return;
        }
        double value = ((FieldOfViewPercept) view).getValue();
        if (value > responseBarometerFieldOfView) {
            responseBarometerFieldOfView = value;
            memorise(MemoryEventType.RESPONSE_BAROMETER_FIELD_OF_VIEW_CHANGED.name(), Double.toString(value));
        }
    }

    /**
     * Overwrites {@link #responseBarometerMessages} with the value of the incoming message
     * if the incoming value is higher.
     * @param msg the incoming emergency message
     */
    private void updateResponseBarometerMessages(Object msg) {
        if (msg == null || !(msg instanceof  EmergencyMessage.EmergencyMessageType)) {
            return;
        }
        double value = ((EmergencyMessage.EmergencyMessageType) msg).getValue();
        if (value > responseBarometerMessages) {
            responseBarometerMessages = value;
            memorise(MemoryEventType.RESPONSE_BAROMETER_MESSAGES_CHANGED.name(), Double.toString(value));
        }
    }

    /**
     * Called by the Jill model when this agent posts a new BDI action
     * to the ABM environment
     */
    @Override
    public void packageAction(String actionID, Object[] parameters) {
        logger.warn("{} ignoring action {}", logPrefix(), actionID);
    }

    /**
     * Called by the Jill model with the status of a BDI action previously
     * posted by this agent to the ABM environment.
     */
    @Override
    public void updateAction(String actionID, ActionContent content) {
        logger.debug("{} received action update: {}", logPrefix(), content);
        if (content.getAction_type().equals(ActionList.DRIVETO)) {
            if (content.getState()== ActionContent.State.PASSED) {
                // Wake up the agent that was waiting for external action to finish
                // FIXME: BDI actions put agent in suspend, which won't work for multiple intention stacks
                suspend(false);
            } else if (content.getState()== ActionContent.State.FAILED) {
                // Wake up the agent that was waiting for external action to finish
                // FIXME: BDI actions put agent in suspend, which won't work for multiple intention stacks
                suspend(false);
            }
        }
    }

    /**
     * BDI-ABM agent init function; Not used by Jill.
     * Use {@link #start(PrintStream, String[])} instead
     * to perform any agent specific initialisation.
     */
    @Override
    public void init(String[] args) {
        parseArgs(args);
    }

    /**
     * BDI-ABM agent start function; Not used by Jill.
     * Use {@link #start(PrintStream, String[])} instead
     * to perform agent startup.
     */
    @Override
    public void start() {
        logger.warn("{} using a stub for io.github.agentsoz.bdiabm.Agent.start()", logPrefix());
    }

    /**
     * BDI-ABM agent kill function; Not used by Jill.
     * Use {@link #finish()} instead
     * to perform agent termination.
     */

    @Override
    public void kill() {
        logger.warn("{} using a stub for io.github.agentsoz.bdiabm.Agent.kill()", logPrefix());
    }

    @Override
    public void setQueryPerceptInterface(QueryPerceptInterface queryInterface) {
        this.queryInterface = queryInterface;
    }

    @Override
    public QueryPerceptInterface getQueryPerceptInterface() {
        return queryInterface;
    }


    double getTime() {
        return time;
    }

    private void parseArgs(String[] args) {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "hasDependents":
                        if (i + 1 < args.length) {
                            i++;
                            try {
                                hasDependents = Boolean.parseBoolean(args[i]);
                            } catch (Exception e) {
                                logger.error("Could not parse boolean '"+ args[i] + "'", e);
                            }
                        }
                        break;
                    case "InitialResponseThreshold":
                        if(i+1<args.length) {
                            i++ ;
                            try {
                                initialResponseThreshold = Double.parseDouble(args[i]);
                                // limit it to between 0 and 1
                                initialResponseThreshold =
                                        (initialResponseThreshold<0.0) ? 0.0 :
                                                (initialResponseThreshold>1.0) ? 1.0 :
                                                        initialResponseThreshold;
                            } catch (Exception e) {
                                logger.error("Could not parse double '"+ args[i] + "'", e);
                            }
                        }
                        break;
                    case "FinalResponseThreshold":
                        if(i+1<args.length) {
                            i++ ;
                            try {
                                finalResponseThreshold = Double.parseDouble(args[i]);
                                // limit it to between 0 and 1
                                finalResponseThreshold =
                                        (finalResponseThreshold<0.0) ? 0.0 :
                                                (finalResponseThreshold>1.0) ? 1.0 :
                                                        finalResponseThreshold;
                            } catch (Exception e) {
                                logger.error("Could not parse double '"+ args[i] + "'", e);
                            }

                        }
                        break;
                    default:
                        // ignore other options
                        break;
                }
            }
        }
    }

    class Prefix{
        public String toString() {
            return String.format("Time %05.0f BushfireAgent %-4s : ", getTime(), getId());
        }
    }

    String logPrefix() {
        return prefix.toString();
    }

    class Dependent {
        private Location location;

    }
}
