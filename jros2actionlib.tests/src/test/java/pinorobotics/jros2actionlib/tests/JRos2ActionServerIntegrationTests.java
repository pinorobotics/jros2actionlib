/*
 * Copyright 2024 jrosactionlib project
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

import id.jros2client.JRos2Client;
import id.jros2client.JRos2ClientFactory;
import id.xfunction.logging.XLogger;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pinorobotics.jros2actionlib.ActionHandler;
import pinorobotics.jros2actionlib.JRos2ActionLibFactory;
import pinorobotics.jros2actionlib.tests.ClientExecutor.Result;
import pinorobotics.jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciActionDefinition;
import pinorobotics.jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciGoalMessage;
import pinorobotics.jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciResultMessage;
import pinorobotics.jros2actionlib.tests.clientexecutors.JRos2ActionClientExecutor;
import pinorobotics.jros2actionlib.tests.clientexecutors.ThirdpartyClientExecutor;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class JRos2ActionServerIntegrationTests {

    private static final JRos2ActionLibFactory actionFactory = new JRos2ActionLibFactory();
    private static final String ACTION_SERVER_NAME = "fibonacci";
    private JRos2Client client;

    @BeforeAll
    public static void setupAll() {
        XLogger.load("jros2actionlib-test.properties");
    }

    @BeforeEach
    public void setup() throws MalformedURLException {
        client = new JRos2ClientFactory().createClient();
    }

    @AfterEach
    public void clean() throws Exception {
        client.close();
    }

    static Stream<ClientExecutor> dataProvider() {
        return Stream.of(
                new ThirdpartyClientExecutor(20),
                new JRos2ActionClientExecutor(1, ACTION_SERVER_NAME));
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test_happy(ClientExecutor clientExecutor) throws Exception {
        ActionHandler<FibonacciGoalMessage, FibonacciResultMessage> handler =
                action ->
                        CompletableFuture.completedFuture(
                                new FibonacciResultMessage().withSequence(fibo(action.order)));
        try (var actionServer =
                actionFactory.createActionServer(
                        client, new FibonacciActionDefinition(), ACTION_SERVER_NAME, handler)) {
            actionServer.start();
            List<Future<Result>> futures = clientExecutor.executeClientsAsync();

            for (Future<Result> future : futures) {
                var result = future.get();
                String actualOutput = result.output();
                int seed = result.seed();
                Assertions.assertEquals(generateExpectedOutput(seed), actualOutput);
            }
        } finally {
            clientExecutor.close();
        }
    }

    public static void test_example_from_documentation() throws IOException {
        var clientFactory = new JRos2ClientFactory();
        var actionlibFactory = new JRos2ActionLibFactory();
        Function<Integer, int[]> fibo =
                order -> {
                    var seq = new int[order];
                    seq[0] = 1;
                    if (seq.length == 1) return seq;
                    seq[1] = 1;
                    if (seq.length == 2) return seq;
                    for (int i = 2; i < seq.length; i++) {
                        seq[i] = seq[i - 1] + seq[i - 2];
                    }
                    return seq;
                };
        ActionHandler<FibonacciGoalMessage, FibonacciResultMessage> handler =
                goal -> {
                    System.out.println("Received new goal " + goal);
                    var result = new FibonacciResultMessage().withSequence(fibo.apply(goal.order));
                    System.out.println("Result " + result);
                    return CompletableFuture.completedFuture(result);
                };
        try (var client = clientFactory.createClient();
                var actionServer =
                        actionlibFactory.createActionServer(
                                client,
                                new FibonacciActionDefinition(),
                                "jros_fibonacci",
                                handler)) {
            actionServer.start();
            System.out.println("Press Enter to stop ROS Action Server...");
            System.in.read();
        }
    }

    private static int[] fibo(int order) {
        var seq = new int[order];
        seq[0] = 1;
        if (seq.length == 1) return seq;
        seq[1] = 1;
        if (seq.length == 2) return seq;
        for (int i = 2; i < seq.length; i++) {
            seq[i] = seq[i - 1] + seq[i - 2];
        }
        return seq;
    }

    private String generateExpectedOutput(int order) {
        return """
        : order=%d
        : Sending goal
        : Goal accepted by server, waiting for result
        : Result received: %s\
        """
                .formatted(
                        order,
                        Arrays.stream(fibo(order))
                                .mapToObj(i -> "" + i)
                                .collect(Collectors.joining(" ")));
    }

    public static void main(String[] args) throws IOException {
        test_example_from_documentation();
    }
}
