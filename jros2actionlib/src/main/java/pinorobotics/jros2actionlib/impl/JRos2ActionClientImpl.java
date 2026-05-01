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
import id.jrosmessages.MessageDescriptor;
import id.xfunction.retry.RetryException;
import id.xfunction.retry.RetryableExecutor;
import java.time.Duration;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;
import pinorobotics.jros2actionlib.JRos2ActionLibFactory;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2Definition;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GetResultRequestMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GoalIdMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GoalMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2ResultMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.StatusType;
import pinorobotics.jros2actionlib.impl.msgs.jros_msgs.Action2SendGoalResponseMessage;
import pinorobotics.jros2services.JRos2ServiceClient;
import pinorobotics.jros2services.JRos2ServicesFactory;
import pinorobotics.jrosactionlib.exceptions.JRosActionLibException;
import pinorobotics.jrosactionlib.impl.AbstractJRosActionClient;
import pinorobotics.jrosactionlib.msgs.ActionGoalMessage;
import pinorobotics.jrosactionlib.msgs.ActionResultMessage;
import pinorobotics.jrosservices.msgs.ServiceDefinition;

/**
 * Client which allows to interact with ROS2 Action Server. It communicates with it via a "ROS
 * Action Protocol"
 *
 * @see JRos2ActionLibFactory Factory for available ROS2 action client implementations
 * @param <G> message type used to represent a goal
 * @param <R> message type sent by ActionServer upon goal completion
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class JRos2ActionClientImpl<G extends Message, R extends Message>
        extends AbstractJRosActionClient<Action2GoalIdMessage, G, R> {

    private static final EnumSet<StatusType> STATUS_TYPE_OK =
            EnumSet.of(StatusType.SUCCEEDED, StatusType.ACCEPTED, StatusType.EXECUTING);
    private JRos2Client client;
    private JRos2ServicesFactory serviceFactory;
    private JRos2ServiceClient<Action2GetResultRequestMessage, Action2ResultMessage<R>>
            getResultServiceClient;
    private JRos2ServiceClient<Action2GoalMessage<G>, Action2SendGoalResponseMessage>
            sendGoalServiceClient;
    private RetryableExecutor executor = new RetryableExecutor();
    private Action2Definition<G, R> action2Definition;

    public JRos2ActionClientImpl(
            JRos2Client client,
            JRos2ServicesFactory serviceFactory,
            Action2Definition<G, R> action2Definition,
            RosName actionServerName) {
        super(action2Definition, actionServerName);
        this.client = client;
        this.serviceFactory = serviceFactory;
        this.action2Definition = action2Definition;
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<R> sendGoalAsync(G arg0) throws JRosActionLibException {
        return super.sendGoalAsync(arg0);
    }

    @Override
    protected void submitGoal(
            Action2GoalIdMessage goalId, ActionGoalMessage<Action2GoalIdMessage, G> actionGoal) {
        logger.info("Send goal with id={0}", goalId);
        try {
            Action2SendGoalResponseMessage result =
                    sendGoalServiceClient
                            .sendRequestAsync((Action2GoalMessage<G>) actionGoal)
                            .get();
            if (STATUS_TYPE_OK.contains(result.getStatus())) {
                logger.info("Goal status id={0} status={1}", goalId, result.getStatus());
            } else {
                throw new JRosActionLibException(
                        "Goal status id=%s status=%s", goalId, result.getStatus());
            }
        } catch (JRosActionLibException | RetryException e) {
            throw e;
        } catch (Exception e) {
            throw new JRosActionLibException(e);
        }
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

    @Override
    protected void onStart() {
        setupGetResultServiceClient();
        setupSendGoalServiceClient();
    }

    private void setupSendGoalServiceClient() {
        String actionGoalRawName = action2Definition.getActionGoalMessage().readNameRaw();
        var actionGoalResultName = new RosName(actionGoalRawName + "Result");
        var serviceResponseMessage =
                new MessageDescriptor<>(Action2SendGoalResponseMessage.class) {
                    @Override
                    public RosName readName() {
                        return actionGoalResultName;
                    }
                };
        var serviceDefinition =
                new ServiceDefinition<Action2GoalMessage<G>, Action2SendGoalResponseMessage>() {
                    @Override
                    public MessageDescriptor getServiceRequestMessage() {
                        return action2Definition.getActionGoalMessage();
                    }

                    @Override
                    public MessageDescriptor<Action2SendGoalResponseMessage>
                            getServiceResponseMessage() {
                        return serviceResponseMessage;
                    }
                };
        sendGoalServiceClient =
                serviceFactory.createClient(client, serviceDefinition, actionServerName.name());
    }

    private void setupGetResultServiceClient() {
        var serviceDefinition =
                new ServiceDefinition<Action2GetResultRequestMessage, Action2ResultMessage<R>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public MessageDescriptor<Action2GetResultRequestMessage>
                            getServiceRequestMessage() {
                        return (MessageDescriptor<Action2GetResultRequestMessage>)
                                action2Definition.getActionResultRequestMessage();
                    }

                    @Override
                    public MessageDescriptor<Action2ResultMessage<R>> getServiceResponseMessage() {
                        return (MessageDescriptor<Action2ResultMessage<R>>)
                                action2Definition.getActionResultMessage();
                    }
                };
        getResultServiceClient =
                serviceFactory.createClient(client, serviceDefinition, actionServerName.name());
    }

    private ActionResultMessage<R> callGetResultInternal(Action2GoalIdMessage goalId)
            throws RetryException {
        logger.info("Calling Get Result for goal with id {0}", goalId);
        try {
            var getResultRequest =
                    action2Definition
                            .getActionResultRequestMessage()
                            .getMessageClass()
                            .getConstructor()
                            .newInstance();
            getResultRequest.withGoalId(goalId);
            var result = getResultServiceClient.sendRequestAsync(getResultRequest).get();
            switch (result.getStatus()) {
                case SUCCEEDED:
                    return result;
                case ACCEPTED, EXECUTING, UNKNOWN:
                    {
                        logger.warning(
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

    @Override
    protected void onClose() {
        getResultServiceClient.close();
        sendGoalServiceClient.close();
    }
}
