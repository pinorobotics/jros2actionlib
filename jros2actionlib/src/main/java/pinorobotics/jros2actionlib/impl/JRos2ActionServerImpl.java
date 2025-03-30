/*
 * Copyright 2024 jrosactionlib project
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
import id.jros2client.qos.PublisherQos;
import id.jros2client.qos.QosDurability;
import id.jros2client.qos.QosReliability;
import id.jrosclient.TopicSubmissionPublisher;
import id.jroscommon.RosName;
import id.jrosmessages.Message;
import id.jrosmessages.MessageDescriptor;
import id.jrosmessages.primitives.Time;
import id.xfunction.Preconditions;
import id.xfunction.logging.XLogger;
import id.xfunction.util.IdempotentService;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import pinorobotics.jros2actionlib.ActionHandler;
import pinorobotics.jros2actionlib.JRos2ActionLibFactory;
import pinorobotics.jros2actionlib.JRos2ActionServer;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2Definition;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GetResultRequestMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GoalMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2ResultMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.StatusType;
import pinorobotics.jros2actionlib.impl.msgs.action_msgs.CancelGoalRequestMessage;
import pinorobotics.jros2actionlib.impl.msgs.action_msgs.CancelGoalResponseMessage;
import pinorobotics.jros2actionlib.impl.msgs.action_msgs.CancelGoalServiceDefinition;
import pinorobotics.jros2actionlib.impl.msgs.action_msgs.GoalStatusArrayMessage;
import pinorobotics.jros2actionlib.impl.msgs.jros_msgs.Action2FeedbackMessage;
import pinorobotics.jros2actionlib.impl.msgs.jros_msgs.Action2SendGoalResponseMessage;
import pinorobotics.jros2services.JRos2Service;
import pinorobotics.jros2services.JRos2ServicesFactory;
import pinorobotics.jrosactionlib.exceptions.JRosActionLibException;
import pinorobotics.jrosservices.msgs.ServiceDefinition;

/**
 * @see JRos2ActionLibFactory factory to create Action Server
 * @param <G> message type used to represent a goal
 * @param <R> message type sent by ActionServer upon goal completion
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class JRos2ActionServerImpl<G extends Message, R extends Message> extends IdempotentService
        implements JRos2ActionServer<G, R> {

    private static final XLogger LOGGER = XLogger.getLogger(JRos2ActionServerImpl.class);
    private final Map<UUID, CompletableFuture<R>> pendingActions = new ConcurrentHashMap<>();
    private final JRos2Client client;
    private final JRos2ServicesFactory serviceFactory;
    private final Action2Definition<G, R> actionDefinition;
    private final RosName actionServerName;
    private final ActionHandler<G, R> actionHandler;
    private final String actionGoalRawName;
    private JRos2Service<Action2GetResultRequestMessage, Action2ResultMessage<R>>
            actionGetResultService;
    private JRos2Service<Action2GoalMessage<G>, Action2SendGoalResponseMessage> sendGoalService;
    private JRos2Service<CancelGoalRequestMessage, CancelGoalResponseMessage> cancelGoalService;
    private TopicSubmissionPublisher<GoalStatusArrayMessage> statusPublisher;
    private TopicSubmissionPublisher<Action2FeedbackMessage> feedbackPublisher;

    public JRos2ActionServerImpl(
            JRos2Client client,
            JRos2ServicesFactory serviceFactory,
            Action2Definition<G, R> actionDefinition,
            RosName actionServerName,
            ActionHandler<G, R> actionHandler) {
        this.client = client;
        this.serviceFactory = serviceFactory;
        this.actionDefinition = actionDefinition;
        this.actionServerName = actionServerName;
        this.actionHandler = actionHandler;
        actionGoalRawName = actionDefinition.getActionGoalMessage().readNameRaw();
        Preconditions.isTrue(
                actionGoalRawName.endsWith("ActionGoal"),
                "Action goal name %s should end with ***ActionGoal",
                actionGoalRawName);
    }

    @Override
    protected void onStart() {
        LOGGER.fine("Start action server {0}", actionServerName);
        startStatusPublisher();
        startFeedbackPublisher();
        startCancelGoalService();
        startGetResultService();
        startSendGoalService();
    }

    @Override
    protected void onClose() {
        LOGGER.fine("Stop action server {0}", actionServerName);
        statusPublisher.close();
        feedbackPublisher.close();
        cancelGoalService.close();
        actionGetResultService.close();
        sendGoalService.close();
    }

    private void startSendGoalService() {
        LOGGER.fine("Start sendGoal service for action {0}", actionServerName);
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
                        return actionDefinition.getActionGoalMessage();
                    }

                    @Override
                    public MessageDescriptor<Action2SendGoalResponseMessage>
                            getServiceResponseMessage() {
                        return serviceResponseMessage;
                    }
                };
        sendGoalService =
                serviceFactory.createService(
                        client, serviceDefinition, actionServerName, this::onSendGoal);
        sendGoalService.start();
    }

    private void startCancelGoalService() {
        LOGGER.fine("Start cancelGoal service for action {0}", actionServerName);
        cancelGoalService =
                serviceFactory.createService(
                        client,
                        new CancelGoalServiceDefinition(),
                        actionServerName,
                        this::onCancelGoal);
        cancelGoalService.start();
    }

    private void startStatusPublisher() {
        LOGGER.fine("Start status publisher for action {0}", actionServerName);
        statusPublisher =
                new TopicSubmissionPublisher<GoalStatusArrayMessage>(
                        new MessageDescriptor<>(GoalStatusArrayMessage.class), actionServerName);
        client.publish(
                new PublisherQos(QosReliability.RELIABLE, QosDurability.TRANSIENT_LOCAL),
                statusPublisher);
    }

    private void startFeedbackPublisher() {
        LOGGER.fine("Start feedback publisher for action {0}", actionServerName);
        var feedbackMessageName = new RosName(actionGoalRawName + "Feedback");
        var desc =
                new MessageDescriptor<>(Action2FeedbackMessage.class) {
                    @Override
                    public RosName readName() {
                        return feedbackMessageName;
                    }
                };
        feedbackPublisher =
                new TopicSubmissionPublisher<Action2FeedbackMessage>(desc, actionServerName);
        client.publish(feedbackPublisher);
    }

    private void startGetResultService() {
        LOGGER.fine("Start getResult service for action {0}", actionServerName);
        var serviceDefinition =
                new ServiceDefinition<Action2GetResultRequestMessage, Action2ResultMessage<R>>() {
                    @Override
                    public MessageDescriptor getServiceRequestMessage() {
                        return actionDefinition.getActionResultRequestMessage();
                    }

                    @Override
                    public MessageDescriptor getServiceResponseMessage() {
                        return actionDefinition.getActionResultMessage();
                    }
                };
        actionGetResultService =
                serviceFactory.createService(
                        client, serviceDefinition, actionServerName, this::onGetActionResult);
        actionGetResultService.start();
    }

    private Action2ResultMessage<R> onGetActionResult(Action2GetResultRequestMessage request)
            throws Exception {
        var uuid = request.getGoalId().uuid;
        LOGGER.fine("Request result for action {0} with UUID {1}", actionServerName, uuid);
        var res = pendingActions.get(uuid);
        var resultMessage = newResultMessage();
        if (res == null) {
            resultMessage.withStatus(StatusType.UNKNOWN);
        } else if (res.isCancelled()) {
            resultMessage.withStatus(StatusType.CANCELED);
        } else if (res.isCompletedExceptionally()) {
            resultMessage.withStatus(StatusType.ABORTED);
        } else if (res.isDone()) {
            resultMessage.withStatus(StatusType.SUCCEEDED);
            resultMessage.withResult(res.get());
        } else {
            resultMessage.withStatus(StatusType.EXECUTING);
        }
        LOGGER.fine(
                "Action {0} with UUID {1} has status {2}",
                actionServerName, uuid, resultMessage.getStatus());
        return resultMessage;
    }

    private Action2SendGoalResponseMessage onSendGoal(Action2GoalMessage<G> goalMessage)
            throws Exception {
        LOGGER.entering("onSendGoal " + actionServerName);
        var uuid = goalMessage.getGoalId().uuid;
        try {
            LOGGER.info("Execute action {0} with UUID {1}", actionServerName, uuid);
            pendingActions.put(uuid, actionHandler.execute(goalMessage.getGoal()));
            return new Action2SendGoalResponseMessage()
                    .withStatus(StatusType.ACCEPTED)
                    .withTimestamp(Time.now());
        } catch (Exception e) {
            throw new JRosActionLibException(
                    "Error executing action %s with UUID %s".formatted(actionServerName, uuid), e);
        } finally {
            LOGGER.exiting("onNext " + actionServerName);
        }
    }

    private CancelGoalResponseMessage onCancelGoal(CancelGoalRequestMessage goalMessage)
            throws Exception {
        LOGGER.fine(
                "Cancel goal request for action {0} with UUID {1}",
                actionServerName, goalMessage.goal_info.goal_id.uuid);
        return new CancelGoalResponseMessage();
    }

    private Action2ResultMessage<R> newResultMessage() {
        try {
            return actionDefinition
                    .getActionResultMessage()
                    .getMessageClass()
                    .getConstructor()
                    .newInstance();
        } catch (InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException
                | NoSuchMethodException
                | SecurityException e) {
            throw new JRosActionLibException("Error creating result message", e);
        }
    }
}
