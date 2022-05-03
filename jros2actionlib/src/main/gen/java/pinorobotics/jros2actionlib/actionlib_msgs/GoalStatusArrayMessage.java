/*
 * Copyright 2021 jrosactionlib project
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

import id.jros2messages.std_msgs.HeaderMessage;
import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.xfunction.XJson;
import java.util.Arrays;
import java.util.Objects;
import pinorobotics.jrosactionlib.actionlib_msgs.GoalStatusMessage;

/**
 * Definition for actionlib_msgs/GoalStatusArray Stores the statuses for goals that are currently
 * being tracked by an action server
 */
@MessageMetadata(name = GoalStatusArrayMessage.NAME, md5sum = "12ef789174f5252c94b427cdb93b2d0a")
public class GoalStatusArrayMessage implements Message {

    static final String NAME = "actionlib_msgs/GoalStatusArray";

    public HeaderMessage header = new HeaderMessage();

    public GoalStatusMessage[] status_list = new GoalStatusMessage[0];

    public GoalStatusArrayMessage withHeader(HeaderMessage header) {
        this.header = header;
        return this;
    }

    public GoalStatusArrayMessage withStatusList(GoalStatusMessage... status_list) {
        this.status_list = status_list;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, Arrays.hashCode(status_list));
    }

    @Override
    public boolean equals(Object obj) {
        var other = (GoalStatusArrayMessage) obj;
        return Objects.equals(header, other.header)
                && Arrays.equals(status_list, other.status_list);
    }

    @Override
    public String toString() {
        return XJson.asString("header", header, "status_list", Arrays.toString(status_list));
    }
}
