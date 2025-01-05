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

import id.jrosmessages.Message;
import pinorobotics.jrosactionlib.msgs.ActionResultMessage;

/**
 * Base interface for all actionlib result messages.
 *
 * <p>Each actionlib result message should consist from status of goal and the result {@link
 * Message}.
 *
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public interface Action2ResultMessage<R extends Message> extends ActionResultMessage<R> {

    Action2ResultMessage<R> withGoalId(StatusType goal_id);

    StatusType getStatus();
}
