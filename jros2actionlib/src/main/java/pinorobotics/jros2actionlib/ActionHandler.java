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
package pinorobotics.jros2actionlib;

import id.jrosmessages.Message;
import java.util.concurrent.CompletableFuture;

/**
 * Action handler is responsible for executing all incoming ROS2 Action requests.
 *
 * <p>Supposed to be implemented by the users.
 *
 * @see <a href="https://docs.ros.org/en/humble/Tutorials/Intermediate/Creating-an-Action.html">ROS2
 *     Action</a>
 * @see JRos2ActionLibFactory Factory for available ROS2 Action implementations
 * @param <G> message type used to represent a goal
 * @param <R> message type sent by ActionServer upon goal completion
 * @author aeon_flux aeon_flux@eclipso.ch
 */
@FunctionalInterface
public interface ActionHandler<G extends Message, R extends Message> {
    /**
     * @return Action result which will be available for the user who requested the Action only when
     *     the returned future completes
     */
    CompletableFuture<R> execute(G request) throws Exception;
}
