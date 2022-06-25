/*
 * Copyright 2022 jrosactionlib project
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

import id.jros2client.JRos2Client;
import id.jrosmessages.Message;
import id.xfunction.logging.XLogger;
import java.util.concurrent.CompletableFuture;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2Definition;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GetResultRequestMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GoalIdMessage;
import pinorobotics.jros2services.JRos2ServiceClient;
import pinorobotics.jrosactionlib.impl.AbstractJRosActionClient;
import pinorobotics.jrosactionlib.msgs.ActionResultMessage;

/**
 * Client which allows to interact with ROS2 Action Server. It communicates with it via a "ROS
 * Action Protocol"
 *
 * @param <G> message type used to represent a goal
 * @param <R> message type sent by ActionServer upon goal completion
 * @see <a href="https://docs.ros.org/en/galactic/Tutorials/Understanding-ROS2-Actions.html">ROS2
 *     Actions</a>
 * @see <a href="http://design.ros2.org/articles/actions.html">ROS2 Actions Implementation</a>
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class JRos2ActionClient<G extends Message, R extends Message>
        extends AbstractJRosActionClient<Action2GoalIdMessage, G, R> {

    private static final XLogger LOGGER = XLogger.getLogger(JRos2ActionClient.class);
    private Action2Definition<G, R> actionDefinition;
    private JRos2ServiceClient<Action2GetResultRequestMessage, ActionResultMessage<R>>
            serviceClient;

    JRos2ActionClient(
            JRos2Client client,
            JRos2ServiceClient<Action2GetResultRequestMessage, ActionResultMessage<R>>
                    serviceClient,
            Action2Definition<G, R> actionDefinition,
            String actionServerName) {
        super(client, actionDefinition, actionServerName);
        this.serviceClient = serviceClient;
        this.actionDefinition = actionDefinition;
    }

    @Override
    protected Action2GoalIdMessage createGoalId() {
        return Action2GoalIdMessage.generate();
    }

    @Override
    protected CompletableFuture<ActionResultMessage<R>> callGetResult(Action2GoalIdMessage goalId)
            throws Exception {
        LOGGER.info("Calling Get Result for goal with id {0}", goalId);
        var getResultRequest =
                actionDefinition.getActionResultRequestMessage().getConstructor().newInstance();
        getResultRequest.withGoalId(goalId);
        return serviceClient.sendRequestAsync(getResultRequest);
    }
}
