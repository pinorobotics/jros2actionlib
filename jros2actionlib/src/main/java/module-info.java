/*
 * Copyright 2022 jrosactionlib project
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
/**
 * Java module which allows to interact with <a
 * href="https://docs.ros.org/en/galactic/Tutorials/Understanding-ROS2-Actions.html">ROS2 (Robot
 * Operating System) Actions</a>.
 *
 * <p>For usage examples see <a href="http://pinoweb.freetzi.com/jrosactionlib">Documentation</a>
 *
 * @see <a href="https://github.com/pinorobotics/jros2actionlib">GitHub repository</a>
 * @see <a
 *     href="https://github.com/pinorobotics/jros2actionlib/blob/main/jros2actionlib/release/CHANGELOG.md">Releases</a>
 * @see <a href="https://docs.ros.org/en/galactic/Tutorials/Understanding-ROS2-Actions.html">ROS2
 *     Actions</a>
 * @see <a href="http://design.ros2.org/articles/actions.html">ROS2 Actions Implementation</a>
 * @author aeon_flux aeon_flux@eclipso.ch
 */
module jros2actionlib {
    requires transitive jrosactionlib;
    requires transitive jros2messages;
    requires transitive jros2client;
    requires id.xfunction;
    requires jrosmessages;
    requires jros2services;

    exports pinorobotics.jros2actionlib;
    exports pinorobotics.jros2actionlib.actionlib_msgs;
}
