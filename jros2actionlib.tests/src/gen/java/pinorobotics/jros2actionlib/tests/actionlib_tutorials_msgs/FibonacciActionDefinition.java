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
package pinorobotics.jros2actionlib.tests.actionlib_tutorials_msgs;

import id.jrosmessages.MessageDescriptor;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2Definition;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GetResultRequestMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GoalMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2ResultMessage;

/** Definition for action_tutorials_interfaces/action/Fibonacci */
public class FibonacciActionDefinition
        implements Action2Definition<FibonacciGoalMessage, FibonacciResultMessage> {
    private static final MessageDescriptor<? extends Action2GoalMessage<FibonacciGoalMessage>>
            GOAL_MESSAGE_DESCRIPTOR = new MessageDescriptor<>(FibonacciActionGoalMessage.class);
    private static final MessageDescriptor<? extends Action2ResultMessage<FibonacciResultMessage>>
            RESULT_MESSAGE_DESCRIPTOR = new MessageDescriptor<>(FibonacciActionResultMessage.class);
    private static final MessageDescriptor<? extends Action2GetResultRequestMessage>
            RESULT_REQUEST_MESSAGE_DESCRIPTOR =
                    new MessageDescriptor<>(FibonacciActionGetResultRequestMessage.class);

    @Override
    public MessageDescriptor<? extends Action2GoalMessage<FibonacciGoalMessage>>
            getActionGoalMessage() {
        return GOAL_MESSAGE_DESCRIPTOR;
    }

    @Override
    public MessageDescriptor<? extends Action2ResultMessage<FibonacciResultMessage>>
            getActionResultMessage() {
        return RESULT_MESSAGE_DESCRIPTOR;
    }

    @Override
    public MessageDescriptor<? extends Action2GetResultRequestMessage>
            getActionResultRequestMessage() {
        return RESULT_REQUEST_MESSAGE_DESCRIPTOR;
    }
}
