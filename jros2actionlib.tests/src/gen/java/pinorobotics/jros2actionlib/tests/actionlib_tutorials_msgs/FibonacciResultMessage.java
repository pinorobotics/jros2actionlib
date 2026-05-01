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
import java.util.Arrays;
import java.util.Objects;

/** Definition for action_tutorials_interfaces/FibonacciResult */
@MessageMetadata(name = FibonacciResultMessage.NAME)
public class FibonacciResultMessage implements Message {

    static final String NAME = "action_tutorials_interfaces/FibonacciResult";

    public int[] sequence = new int[0];

    public FibonacciResultMessage withSequence(int... sequence) {
        this.sequence = sequence;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(sequence));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FibonacciResultMessage other
                && Arrays.equals(sequence, other.sequence);
    }

    @Override
    public String toString() {
        return XJson.asString("sequence", sequence);
    }
}
