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

import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.xfunction.XJson;
import java.util.Objects;

/** Definition for action_tutorials_interfaces/FibonacciGoal */
@MessageMetadata(name = FibonacciGoalMessage.NAME)
public class FibonacciGoalMessage implements Message {

    static final String NAME = "action_tutorials_interfaces/FibonacciGoal";

    public int order;

    public FibonacciGoalMessage withOrder(int order) {
        this.order = order;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(order);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FibonacciGoalMessage other && order == other.order;
    }

    @Override
    public String toString() {
        return XJson.asString("order", order);
    }
}
