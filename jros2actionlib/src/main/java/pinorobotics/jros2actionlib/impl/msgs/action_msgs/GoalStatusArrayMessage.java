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
package pinorobotics.jros2actionlib.impl.msgs.action_msgs;

import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.jrosmessages.RosInterfaceType;
import id.xfunction.XJson;
import java.util.Arrays;
import java.util.Objects;

/** Definition for action_msgs/GoalStatusArray */
@MessageMetadata(name = GoalStatusArrayMessage.NAME, interfaceType = RosInterfaceType.ACTION)
public class GoalStatusArrayMessage implements Message {

    static final String NAME = "action_msgs/GoalStatusArray";

    /** An array of goal statuses. */
    public GoalStatusMessage[] status_list = new GoalStatusMessage[0];

    public GoalStatusArrayMessage withStatusList(GoalStatusMessage... status_list) {
        this.status_list = status_list;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(status_list));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GoalStatusArrayMessage other)
            return Arrays.equals(status_list, other.status_list);
        return false;
    }

    @Override
    public String toString() {
        return XJson.asString("status_list", status_list);
    }
}
