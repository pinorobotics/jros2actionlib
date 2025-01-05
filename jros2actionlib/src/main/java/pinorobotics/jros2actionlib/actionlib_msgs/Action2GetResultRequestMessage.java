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
import pinorobotics.jrosactionlib.msgs.ActionResultMessage;

/**
 * Definition for ROS2 {@link Message} which is used to request {@link ActionResultMessage} from the
 * Action Server.
 *
 * <p>In ROS2 even though {@link Action2GetResultRequestMessage} has same structure (fields) across
 * all actions, the type names of each {@link Action2GetResultRequestMessage} are different (for
 * example: action_tutorials_interfaces/FibonacciActionGetResult). For that reason each {@link
 * Action2GetResultRequestMessage} needs to be implemented for each action separately.
 */
public interface Action2GetResultRequestMessage extends Message {

    Action2GetResultRequestMessage withGoalId(Action2GoalIdMessage goal_id);

    Action2GoalIdMessage getGoalId();
}
