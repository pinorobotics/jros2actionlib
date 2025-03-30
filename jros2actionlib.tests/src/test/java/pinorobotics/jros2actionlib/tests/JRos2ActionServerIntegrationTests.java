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
import id.xfunction.lang.XExec;
import id.xfunction.lang.XProcess;
import id.xfunction.logging.XLogger;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pinorobotics.jros2actionlib.ActionHandler;
import pinorobotics.jros2actionlib.JRos2ActionLibFactory;
import pinorobotics.jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciActionDefinition;
import pinorobotics.jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciGoalMessage;
import pinorobotics.jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciResultMessage;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class JRos2ActionServerIntegrationTests {

    private static final JRos2ActionLibFactory actionFactory = new JRos2ActionLibFactory();
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

    @Test
    public void test_happy() throws Exception {
        var seeds = new Random().ints(13, 1, 20).distinct().mapToObj(Integer::valueOf).toList();
        var clientLogs = Files.createTempDirectory("jros2actionlib");
        var clientProcs =
                seeds.stream()
                        .map(
                                seed ->
                                        new XExec(
                                                        "bash",
                                                        "-c",
                                                        """
cd ws2/out.%s &&
COLCON_CURRENT_PREFIX=./install source install/setup.sh &&
build/action_tutorials_cpp/fibonacci_action_client --ros-args --log-level DEBUG -p order:=%d
"""
                                                                .formatted(
                                                                        System.getenv("ROS_DISTRO"),
                                                                        seed))
                                                .start()
                                                .stderrAsync(newLogger(clientLogs, seed)))
                        .toList();

        ActionHandler<FibonacciGoalMessage, FibonacciResultMessage> handler =
                action ->
                        CompletableFuture.completedFuture(
                                new FibonacciResultMessage(fibo(action.order)));
        try (var actionServer =
                actionFactory.createActionServer(
                        client, new FibonacciActionDefinition(), "fibonacci", handler)) {
            actionServer.start();
            for (int i = 0; i < seeds.size(); i++) {
                var proc = clientProcs.get(i);
                Assertions.assertEquals(0, proc.await());
                var actualOutput =
                        Files.readAllLines(clientLogs.resolve("" + seeds.get(i))).stream()
                                .filter(l -> l.startsWith("[INFO]"))
                                .filter(l -> !l.contains("waiting for service to appear"))
                                .map(l -> l.replaceAll("\\[.*\\]", "").strip())
                                .collect(Collectors.joining("\n"));
                Assertions.assertEquals(generateExpectedOutput(seeds.get(i)), actualOutput);
            }
        } finally {
            clientProcs.forEach(XProcess::destroyAllForcibly);
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
                    var result = new FibonacciResultMessage(fibo.apply(goal.order));
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
               : Result received: %s """
                .formatted(
                        order,
                        Arrays.stream(fibo(order))
                                .mapToObj(i -> "" + i)
                                .collect(Collectors.joining(" ")));
    }

    private Consumer<String> newLogger(Path clientLogs, int seed) {
        return line -> {
            try {
                Files.writeString(
                        clientLogs.resolve("" + seed),
                        line + "\n",
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
                System.out.println(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public static void main(String[] args) throws IOException {
        test_example_from_documentation();
    }
}
