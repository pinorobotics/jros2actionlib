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
import id.jrosclient.RosVersion;
import id.jroscommon.RosName;
import id.jrosmessages.Message;
import id.jrosmessages.MessageDescriptor;
import id.xfunction.Preconditions;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2Definition;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GetResultRequestMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2ResultMessage;
import pinorobotics.jros2actionlib.impl.JRos2ActionClientImpl;
import pinorobotics.jros2actionlib.impl.JRos2ActionServerImpl;
import pinorobotics.jros2services.JRos2ServicesFactory;
import pinorobotics.jrosactionlib.JRosActionClient;
import pinorobotics.jrosservices.msgs.ServiceDefinition;

/**
 * Factory methods of <b>jros2actionlib</b> module.
 *
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class JRos2ActionFactory {

    /**
     * Create client for ROS2 Action Server
     *
     * @param client ROS client
     * @param actionDefinition message type definitions for an action
     * @param actionServerName name of the action server which will execute the actions
     * @param <G> message type used to represent a goal
     * @param <R> message type sent by ActionServer upon goal completion
     */
    public <G extends Message, R extends Message> JRosActionClient<G, R> createClient(
            JRos2Client client, Action2Definition<G, R> actionDefinition, String actionServerName) {
        Preconditions.isTrue(
                client.getSupportedRosVersion().contains(RosVersion.ROS2), "Requires ROS2 client");

        var factory = new JRos2ServicesFactory();
        var serviceDefinition =
                new ServiceDefinition<Action2GetResultRequestMessage, Action2ResultMessage<R>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public MessageDescriptor<Action2GetResultRequestMessage>
                            getServiceRequestMessage() {
                        return (MessageDescriptor<Action2GetResultRequestMessage>)
                                actionDefinition.getActionResultRequestMessage();
                    }

                    @Override
                    public MessageDescriptor<Action2ResultMessage<R>> getServiceResponseMessage() {
                        return (MessageDescriptor<Action2ResultMessage<R>>)
                                actionDefinition.getActionResultMessage();
                    }
                };
        var serviceClient = factory.createClient(client, serviceDefinition, actionServerName);
        return new JRos2ActionClientImpl<>(
                client, serviceClient, actionDefinition, new RosName(actionServerName));
    }

    /**
     * Create ROS2 Action Server
     *
     * <p>Action Server needs to be started explicitly with {@link JRos2ActionServer#start()}.
     *
     * @param client ROS2 client
     * @param actionDefinition message type definitions for an action server
     * @param actionServerName name of the action server which will execute the actions
     * @param actionHandler action handler which will process all incoming ROS actions
     * @param <G> message type used to represent a goal
     * @param <R> message type sent by ActionServer upon goal completion
     */
    public <G extends Message, R extends Message> JRos2ActionServer<G, R> createActionServer(
            JRos2Client client,
            Action2Definition<G, R> actionDefinition,
            RosName actionServerName,
            ActionHandler<G, R> actionHandler) {
        return new JRos2ActionServerImpl<>(
                client,
                new JRos2ServicesFactory(),
                actionDefinition,
                actionServerName,
                actionHandler);
    }

    /**
     * Simplified version of {@link #createActionServer(JRos2Client, Action2Definition, RosName,
     * ActionHandler)} where topic is converted to {@link RosName}
     */
    public <G extends Message, R extends Message> JRos2ActionServer<G, R> createActionServer(
            JRos2Client client,
            Action2Definition<G, R> actionDefinition,
            String actionServerName,
            ActionHandler<G, R> actionHandler) {
        return createActionServer(
                client, actionDefinition, new RosName(actionServerName), actionHandler);
    }
}
