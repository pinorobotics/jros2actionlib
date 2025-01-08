Based on [Writing an action server and client](https://docs.ros.org/en/galactic/Tutorials/Actions/Writing-a-Cpp-Action-Server-Client.html)

# Prereqs

``` bash
sudo apt install ros-humble-action-tutorials-cpp --ros-args --log-level DEBUG
```

# Build

```
colcon build
. install/setup.zsh
ros2 run action_tutorials_cpp fibonacci_action_server --ros-args --log-level DEBUG
```

# Setup

**jros2actionlib** tests expect "build" and "install" folders to be placed under "out.<ROS_DISTRO>" folder.

``` bash
rm -rf out.$ROS_DISTRO
mkdir out.$ROS_DISTRO
cp -rf build out.$ROS_DISTRO
cp -rf install out.$ROS_DISTRO
```