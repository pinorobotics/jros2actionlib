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

/**
 * @see JRos2ActionFactory factory to create Action Server
 * @param <G> message type used to represent a goal
 * @param <R> message type sent by ActionServer upon goal completion
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public interface JRos2ActionServer<G extends Message, R extends Message> extends AutoCloseable {

    /**
     * Start ROS Action Server
     *
     * <p>Once started, Action Server becomes active and accepts the requests until it is closed
     * with {@link #close()}
     */
    void start();

    /** Stop ROS Action Server */
    @Override
    void close();
}
