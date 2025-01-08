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
import id.xfunction.XJson;
import java.util.Objects;

/**
 * Definition for action_msgs/GoalStatus
 *
 * <p>An action goal can be in one of these states after it is accepted by an action server.
 *
 * <p>For more information, see http://design.ros2.org/articles/actions.html
 */
@MessageMetadata(
        name = GoalStatusMessage.NAME,
        fields = {"goal_info", "status"})
public class GoalStatusMessage implements Message {

    static final String NAME = "action_msgs/GoalStatus";

    public enum StatusType {
        /** Indicates status has not been properly set. */
        STATUS_UNKNOWN,

        /** The goal has been accepted and is awaiting execution. */
        STATUS_ACCEPTED,

        /** The goal is currently being executed by the action server. */
        STATUS_EXECUTING,

        /**
         * The client has requested that the goal be canceled and the action server has accepted the
         * cancel request.
         */
        STATUS_CANCELING,

        /** The goal was achieved successfully by the action server. */
        STATUS_SUCCEEDED,

        /** The goal was canceled after an external request from an action client. */
        STATUS_CANCELED,

        /** The goal was terminated by the action server without an external request. */
        STATUS_ABORTED,
    }

    /** Goal info (contains ID and timestamp). */
    public GoalInfoMessage goal_info = new GoalInfoMessage();

    /** Action goal state-machine status. */
    public byte status;

    public GoalStatusMessage withGoalInfo(GoalInfoMessage goal_info) {
        this.goal_info = goal_info;
        return this;
    }

    public GoalStatusMessage withStatus(StatusType status) {
        this.status = (byte) status.ordinal();
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(goal_info, status);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GoalStatusMessage other)
            return Objects.equals(goal_info, other.goal_info) && status == other.status;
        return false;
    }

    @Override
    public String toString() {
        return XJson.asString(
                "goal_info", goal_info,
                "status", status);
    }
}
