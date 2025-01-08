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
import id.xfunction.XJson;
import java.util.Objects;

/**
 * Cancel one or more goals with the following policy:
 *
 * <ul>
 *   <li>If the goal ID is zero and timestamp is zero, cancel all goals.
 *   <li>If the goal ID is zero and timestamp is not zero, cancel all goals accepted at or before
 *       the timestamp.
 *   <li>If the goal ID is not zero and timestamp is zero, cancel the goal with the given ID
 *       regardless of the time it was accepted.
 *   <li>If the goal ID is not zero and timestamp is not zero, cancel the goal with the given ID and
 *       all goals accepted at or before the timestamp.
 * </ul>
 *
 * @author aeon_flux aeon_flux@eclipso.ch
 */
@MessageMetadata(name = CancelGoalRequestMessage.NAME, interfaceType = RosInterfaceType.ACTION)
public class CancelGoalRequestMessage implements Message {

    static final String NAME = "action_msgs/CancelGoalServiceRequest";

    /** Goal info describing the goals to cancel, see above. */
    public GoalInfoMessage goal_info;

    public CancelGoalRequestMessage withGoalInfo(GoalInfoMessage goal_info) {
        this.goal_info = goal_info;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(goal_info);
    }

    @Override
    public boolean equals(Object obj) {
        var other = (CancelGoalRequestMessage) obj;
        return Objects.equals(goal_info, other.goal_info);
    }

    @Override
    public String toString() {
        return XJson.asString("goal_info", goal_info);
    }
}
