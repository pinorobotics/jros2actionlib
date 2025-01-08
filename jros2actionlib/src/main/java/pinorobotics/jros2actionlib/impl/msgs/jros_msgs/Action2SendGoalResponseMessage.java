/*
 * Copyright 2025 jrosactionlib project
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
package pinorobotics.jros2actionlib.impl.msgs.jros_msgs;

import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.jrosmessages.RosInterfaceType;
import id.jrosmessages.primitives.Time;
import id.xfunction.XJson;
import java.util.Objects;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GoalMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.StatusType;

/**
 * Response message which is returned by the "Send Goal Service" running inside ROS2 Action Server.
 *
 * <p>The request message of "Send Goal Service" is represented with {@link Action2GoalMessage}
 *
 * @see <a href="https://design.ros2.org/articles/actions.html">Actions</a>
 * @author aeon_flux aeon_flux@eclipso.ch
 */
@MessageMetadata(
        name = Action2SendGoalResponseMessage.NAME,
        fields = {"status", "timestamp"},
        interfaceType = RosInterfaceType.ACTION)
public class Action2SendGoalResponseMessage implements Message {

    static final String NAME = "n/a";

    public byte status;

    public Time timestamp;

    public Action2SendGoalResponseMessage withStatus(StatusType status) {
        this.status = (byte) status.ordinal();
        return this;
    }

    public Action2SendGoalResponseMessage withTimestamp(Time time) {
        this.timestamp = time;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, timestamp);
    }

    @Override
    public boolean equals(Object obj) {
        var other = (Action2SendGoalResponseMessage) obj;
        return Objects.equals(status, other.status) && Objects.equals(timestamp, other.timestamp);
    }

    @Override
    public String toString() {
        return XJson.asString(
                "status", status,
                "timestamp", timestamp);
    }
}
