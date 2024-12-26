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
package pinorobotics.jros2actionlib.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.jros2client.JRos2Client;
import id.jros2client.JRos2ClientFactory;
import id.xfunction.ResourceUtils;
import id.xfunction.lang.XExec;
import id.xfunction.lang.XProcess;
import id.xfunction.logging.XLogger;
import java.net.MalformedURLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pinorobotics.jros2actionlib.JRos2ActionClientFactory;
import pinorobotics.jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciActionDefinition;
import pinorobotics.jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciGoalMessage;
import pinorobotics.jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciResultMessage;
import pinorobotics.jrosactionlib.JRosActionClient;

public class JRos2ActionClientIntegrationTests {

    private static final ResourceUtils resourceUtils = new ResourceUtils();
    private static JRos2Client client;
    private JRosActionClient<FibonacciGoalMessage, FibonacciResultMessage> actionClient;
    private XProcess service;

    @BeforeAll
    public static void setupAll() {
        XLogger.load("jros2actionlib-test.properties");
    }

    @BeforeEach
    public void setup() throws MalformedURLException {
        service =
                new XExec(
                                "bash",
                                "-c",
                                """
                                cd ws2/out.%s &&
                                COLCON_CURRENT_PREFIX=./install source install/setup.sh &&
                                build/action_tutorials_cpp/fibonacci_action_server
                                """
                                        .formatted(System.getenv("ROS_DISTRO")))
                        .start()
                        .forwardOutputAsync(true);
        client = new JRos2ClientFactory().createClient();
        actionClient =
                new JRos2ActionClientFactory()
                        .createClient(client, new FibonacciActionDefinition(), "fibonacci");
    }

    @AfterEach
    public void clean() throws Exception {
        actionClient.close();
        client.close();
        service.destroyAllForcibly();
    }

    @Test
    public void test_sendGoal() throws Exception {
        var goal = new FibonacciGoalMessage().withOrder(13);
        var result = actionClient.sendGoalAsync(goal).get();
        System.out.println(result);
        assertEquals(resourceUtils.readResource("test_sendGoal"), result.toString());
    }
}
