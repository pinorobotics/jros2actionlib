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

import id.jrosmessages.MessageDescriptor;
import pinorobotics.jrosservices.msgs.ServiceDefinition;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class CancelGoalServiceDefinition
        implements ServiceDefinition<CancelGoalRequestMessage, CancelGoalResponseMessage> {

    @Override
    public MessageDescriptor<CancelGoalRequestMessage> getServiceRequestMessage() {
        return new MessageDescriptor<>(CancelGoalRequestMessage.class);
    }

    @Override
    public MessageDescriptor<CancelGoalResponseMessage> getServiceResponseMessage() {
        return new MessageDescriptor<>(CancelGoalResponseMessage.class);
    }
}
