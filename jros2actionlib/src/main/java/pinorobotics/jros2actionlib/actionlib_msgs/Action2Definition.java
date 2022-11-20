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
package pinorobotics.jros2actionlib.actionlib_msgs;

import id.jrosmessages.Message;
import pinorobotics.jrosactionlib.msgs.ActionDefinition;
import pinorobotics.jrosactionlib.msgs.ActionResultMessage;

/**
 * @see <a href="https://design.ros2.org/articles/actions.html">Actions</a>
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public interface Action2Definition<G extends Message, R extends Message>
        extends ActionDefinition<Action2GoalIdMessage, G, R> {

    @Override
    Class<? extends Action2GoalMessage<G>> getActionGoalMessage();

    @Override
    Class<? extends Action2ResultMessage<R>> getActionResultMessage();

    /**
     * In ROS2 Action Server does not return the {@link ActionResultMessage} after goal completes.
     * Action Client needs to request Action Server to return it (see <a
     * href="http://design.ros2.org/articles/actions.html">Get Result Service</a>) sending {@link
     * Action2GetResultRequestMessage} with its goal id.
     *
     * <p>This method returns type of the {@link Message} which is used to request the goals for an
     * Action described by this definition
     */
    Class<? extends Action2GetResultRequestMessage> getActionResultRequestMessage();
}
