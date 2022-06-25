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

import id.jros2messages.unique_identifier_msgs.UUIDMessage;
import java.util.UUID;
import pinorobotics.jrosactionlib.msgs.ActionGoalIdMessage;

/** Definition for ROS2 {@link ActionGoalIdMessage}. */
public class Action2GoalIdMessage extends UUIDMessage implements ActionGoalIdMessage {

    public Action2GoalIdMessage() {}

    public Action2GoalIdMessage(UUID uuid) {
        super(uuid);
    }

    public static Action2GoalIdMessage generate() {
        return new Action2GoalIdMessage(UUID.randomUUID());
    }
}
