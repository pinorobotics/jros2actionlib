Based on [Writing an action server and client](https://docs.ros.org/en/galactic/Tutorials/Actions/Writing-a-Cpp-Action-Server-Client.html)

# Prereqs

``` bash
sudo apt install ros-humble-action-tutorials-cpp
```

# Build

```
colcon build
. install/setup.zsh
ros2 run action_tutorials_cpp fibonacci_action_server
```

# Setup

**jros2actionlib** tests expect "build" and "install" folders to be placed under "out.<ROS_DISTRO>" folder.

``` bash
mkdir out.$ROS_DISTRO
mv -rf build out.$ROS_DISTRO
mv -rf install out.$ROS_DISTRO
```