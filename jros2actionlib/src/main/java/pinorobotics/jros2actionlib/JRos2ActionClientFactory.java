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
import id.jrosmessages.Message;
import id.xfunction.Preconditions;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2Definition;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GetResultRequestMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2ResultMessage;
import pinorobotics.jros2services.JRos2ServiceClientFactory;
import pinorobotics.jros2services.service_msgs.ServiceDefinition;

/**
 * Factory methods for {@link JRos2ActionClient}
 *
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class JRos2ActionClientFactory {

    /**
     * Create client for ROS2 Action Server
     *
     * @param client ROS client
     * @param actionDefinition message type definitions for an action
     * @param actionServerName name of the action server which will execute the actions
     * @param <G> message type used to represent a goal
     * @param <R> message type sent by ActionServer upon goal completion
     */
    public <G extends Message, R extends Message> JRos2ActionClient<G, R> createClient(
            JRos2Client client, Action2Definition<G, R> actionDefinition, String actionServerName) {
        Preconditions.isTrue(
                client.getSupportedRosVersion().contains(RosVersion.ROS2), "Requires ROS2 client");

        var factory = new JRos2ServiceClientFactory();
        var serviceDefinition =
                new ServiceDefinition<Action2GetResultRequestMessage, Action2ResultMessage<R>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public Class<Action2GetResultRequestMessage> getServiceRequestMessage() {
                        return (Class<Action2GetResultRequestMessage>)
                                actionDefinition.getActionResultRequestMessage();
                    }

                    @Override
                    public Class<Action2ResultMessage<R>> getServiceResponseMessage() {
                        return (Class<Action2ResultMessage<R>>)
                                actionDefinition.getActionResultMessage();
                    }
                };
        var serviceClient = factory.createClient(client, serviceDefinition, actionServerName);
        return new JRos2ActionClient<>(client, serviceClient, actionDefinition, actionServerName);
    }
}
