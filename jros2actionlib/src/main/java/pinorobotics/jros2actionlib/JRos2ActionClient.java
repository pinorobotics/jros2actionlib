/*
 * Copyright 2021 jrosactionlib project
 * 
 * Website: https://github.com/pinorobotics/jros2actionlib
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pinorobotics.jros2actionlib;

import id.jrosclient.JRosClient;
import id.jrosmessages.Message;
import id.xfunction.logging.XLogger;
import pinorobotics.jros2actionlib.msgs.GoalIdMessage;
import pinorobotics.jrosactionlib.JRosActionClient;
import pinorobotics.jrosactionlib.msgs.ActionDefinition;
import pinorobotics.jrosactionlib.msgs.GoalId;

/**
 * Client which allows to interact with ROS2 Action Server. It communicates with it via a "ROS
 * Action Protocol"
 *
 * @see <a href="http://wiki.ros.org/actionlib/DetailedDescription">Actionlib</a>
 * @param <G> message type used to represent a goal
 * @param <R> message type sent by ActionServer upon goal completion
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class JRos2ActionClient<G extends Message, R extends Message>
        extends JRosActionClient<G, R> {

    private static final XLogger LOGGER = XLogger.getLogger(JRos2ActionClient.class);

    /**
     * Creates a new instance of the client
     *
     * @param client ROS client
     * @param actionDefinition message type definitions for an action
     * @param actionServerName name of the action server which will execute the actions
     */
    JRos2ActionClient(
            JRosClient client, ActionDefinition<G, R> actionDefinition, String actionServerName) {
        super(client, actionDefinition, actionServerName);
    }

    @Override
    protected GoalId createGoalId() {
        return GoalIdMessage.generate();
    }

    @Override
    protected String asSendGoalTopicName(String actionServerName) {
        return actionServerName + "/_action/send_goalRequest";
    }
}
