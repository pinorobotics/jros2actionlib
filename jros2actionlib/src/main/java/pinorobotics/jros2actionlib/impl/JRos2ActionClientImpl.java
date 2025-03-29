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
package pinorobotics.jros2actionlib.impl;

import id.jros2client.JRos2Client;
import id.jroscommon.RosName;
import id.jrosmessages.Message;
import id.xfunction.logging.XLogger;
import id.xfunction.retry.RetryException;
import id.xfunction.retry.RetryableExecutor;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import pinorobotics.jros2actionlib.JRos2ActionFactory;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2Definition;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GetResultRequestMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GoalIdMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2ResultMessage;
import pinorobotics.jros2services.JRos2ServiceClient;
import pinorobotics.jrosactionlib.exceptions.JRosActionLibException;
import pinorobotics.jrosactionlib.impl.AbstractJRosActionClient;
import pinorobotics.jrosactionlib.msgs.ActionResultMessage;

/**
 * Client which allows to interact with ROS2 Action Server. It communicates with it via a "ROS
 * Action Protocol"
 *
 * @see JRos2ActionFactory Factory for available ROS2 action client implementations
 * @param <G> message type used to represent a goal
 * @param <R> message type sent by ActionServer upon goal completion
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class JRos2ActionClientImpl<G extends Message, R extends Message>
        extends AbstractJRosActionClient<Action2GoalIdMessage, G, R> {

    private static final XLogger LOGGER = XLogger.getLogger(JRos2ActionClientImpl.class);
    private Action2Definition<G, R> actionDefinition;
    private JRos2ServiceClient<Action2GetResultRequestMessage, Action2ResultMessage<R>>
            serviceClient;
    private RetryableExecutor executor = new RetryableExecutor();

    public JRos2ActionClientImpl(
            JRos2Client client,
            JRos2ServiceClient<Action2GetResultRequestMessage, Action2ResultMessage<R>>
                    serviceClient,
            Action2Definition<G, R> actionDefinition,
            RosName actionServerName) {
        super(client, actionDefinition, actionServerName);
        this.serviceClient = serviceClient;
        this.actionDefinition = actionDefinition;
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<R> sendGoalAsync(G arg0) throws JRosActionLibException {
        return super.sendGoalAsync(arg0);
    }

    /**
     * @hidden exclude from javadoc
     */
    @Override
    protected Action2GoalIdMessage createGoalId() {
        return Action2GoalIdMessage.generate();
    }

    /**
     * @hidden exclude from javadoc
     */
    @Override
    protected CompletableFuture<? extends ActionResultMessage<R>> callGetResult(
            Action2GoalIdMessage goalId) {
        return executor.retryIndefinitelyAsync(
                () -> callGetResultInternal(goalId), Duration.ofSeconds(1));
    }

    private ActionResultMessage<R> callGetResultInternal(Action2GoalIdMessage goalId)
            throws RetryException {
        LOGGER.info("Calling Get Result for goal with id {0}", goalId);
        try {
            var getResultRequest =
                    actionDefinition
                            .getActionResultRequestMessage()
                            .getMessageClass()
                            .getConstructor()
                            .newInstance();
            getResultRequest.withGoalId(goalId);
            var result = serviceClient.sendRequestAsync(getResultRequest).get();
            switch (result.getStatus()) {
                case SUCCEEDED:
                    return result;
                case ACCEPTED, EXECUTING, UNKNOWN:
                    {
                        LOGGER.warning(
                                "Result for goal with id {0} is {1}, retrying...",
                                goalId, result.getStatus());
                        throw new RetryException();
                    }
                default:
                    throw new JRosActionLibException(
                            "Action server returned status %s", result.getStatus());
            }
        } catch (JRosActionLibException | RetryException e) {
            throw e;
        } catch (Exception e) {
            throw new JRosActionLibException(e);
        }
    }
}
