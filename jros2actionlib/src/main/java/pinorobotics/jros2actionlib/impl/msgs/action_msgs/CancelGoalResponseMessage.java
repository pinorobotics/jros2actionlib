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
import java.util.Arrays;
import java.util.Objects;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
@MessageMetadata(
        name = CancelGoalResponseMessage.NAME,
        fields = {"return_code", "goals_canceling"},
        interfaceType = RosInterfaceType.ACTION)
public class CancelGoalResponseMessage implements Message {

    static final String NAME = "action_msgs/CancelGoalServiceResponse";

    public static enum ReturnCode {

        /**
         * Indicates the request was accepted without any errors.
         *
         * <p>One or more goals have transitioned to the CANCELING state. The goals_canceling list
         * is not empty.
         */
        ERROR_NONE,

        /**
         * Indicates the request was rejected.
         *
         * <p>No goals have transitioned to the CANCELING state. The goals_canceling list is empty.
         */
        ERROR_REJECTED,

        /**
         * Indicates the requested goal ID does not exist.
         *
         * <p>No goals have transitioned to the CANCELING state. The goals_canceling list is empty.
         */
        ERROR_UNKNOWN_GOAL_ID,

        /**
         * Indicates the goal is not cancelable because it is already in a terminal state.
         *
         * <p>No goals have transitioned to the CANCELING state. The goals_canceling list is empty.
         */
        ERROR_GOAL_TERMINATED
    }

    /** Return code, see {@link ReturnCode} definitions. */
    public byte return_code;

    /** Goals that accepted the cancel request. */
    public GoalInfoMessage[] goals_canceling = new GoalInfoMessage[0];

    @Override
    public int hashCode() {
        return Objects.hash(return_code, goals_canceling);
    }

    @Override
    public boolean equals(Object obj) {
        var other = (CancelGoalResponseMessage) obj;
        return Objects.equals(return_code, other.return_code)
                && Arrays.equals(goals_canceling, other.goals_canceling);
    }

    @Override
    public String toString() {
        return XJson.asString(
                "return_code", return_code,
                "goals_canceling", goals_canceling);
    }
}
