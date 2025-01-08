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
package pinorobotics.jros2actionlib.impl.msgs.action_msgs;

import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.jrosmessages.RosInterfaceType;
import id.jrosmessages.primitives.Time;
import id.xfunction.XJson;
import java.util.Objects;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GoalIdMessage;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
@MessageMetadata(
        name = GoalInfoMessage.NAME,
        fields = {"goal_id", "stamp"},
        interfaceType = RosInterfaceType.MESSAGE)
public class GoalInfoMessage implements Message {

    static final String NAME = "action_msgs/GoalInfo";

    public Action2GoalIdMessage goal_id;

    /** Time when the goal was accepted */
    public Time stamp;

    public GoalInfoMessage withGoalId(Action2GoalIdMessage goal_id) {
        this.goal_id = goal_id;
        return this;
    }

    public GoalInfoMessage withTimestamp(Time stamp) {
        this.stamp = stamp;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(goal_id, stamp);
    }

    @Override
    public boolean equals(Object obj) {
        var other = (GoalInfoMessage) obj;
        return Objects.equals(goal_id, other.goal_id) && Objects.equals(stamp, other.stamp);
    }

    @Override
    public String toString() {
        return XJson.asString(
                "goal_id", goal_id,
                "stamp", stamp);
    }
}
