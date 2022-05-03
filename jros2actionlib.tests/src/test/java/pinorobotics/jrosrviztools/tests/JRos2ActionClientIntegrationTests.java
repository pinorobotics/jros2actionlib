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
package pinorobotics.jrosrviztools.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.jros2client.JRos2ClientFactory;
import id.jrosclient.JRosClient;
import id.jrosmessages.std_msgs.Int32Message;
import id.xfunction.ResourceUtils;
import id.xfunction.logging.XLogger;
import java.net.MalformedURLException;
import jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciActionDefinition;
import jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciGoalMessage;
import jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciResultMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import pinorobotics.jros2actionlib.JRos2ActionClientFactory;
import pinorobotics.jrosactionlib.JRosActionClient;

public class JRos2ActionClientIntegrationTests {

    private static final ResourceUtils resourceUtils = new ResourceUtils();
    private static JRosClient client;
    private JRosActionClient<FibonacciGoalMessage, FibonacciResultMessage> actionClient;

    @BeforeAll
    public static void setupAll() {
        XLogger.load("jros2actionlib-test.properties");
    }

    @BeforeEach
    public void setup() throws MalformedURLException {
        client = new JRos2ClientFactory().createJRosClient();
        actionClient =
                new JRos2ActionClientFactory()
                        .createJRosActionClient(
                                client, new FibonacciActionDefinition(), "fibonacci");
    }

    @AfterEach
    public void clean() throws Exception {
        actionClient.close();
        client.close();
    }

    // @Test
    public void test_sendGoal() throws Exception {
        var goal = new FibonacciGoalMessage().withOrder(new Int32Message().withData(13));
        var result = actionClient.sendGoal(goal).get();
        System.out.println(result);
        assertEquals(resourceUtils.readResource("test_sendGoal"), result.toString());
    }
}
