/*
 * Copyright 2020 jrosactionlib project
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
import id.jrosclient.RosVersion;
import id.jrosmessages.Message;
import id.xfunction.Preconditions;
import pinorobotics.jrosactionlib.JRosActionClient;
import pinorobotics.jrosactionlib.msgs.ActionDefinition;

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
    public <G extends Message, R extends Message> JRosActionClient<G, R> createJRosActionClient(
            JRosClient client, ActionDefinition<G, R> actionDefinition, String actionServerName) {
        Preconditions.isTrue(
                client.getSupportedRosVersion().contains(RosVersion.ROS2), "Requires ROS2 client");
        return new JRos2ActionClient<>(client, actionDefinition, actionServerName);
    }
}
