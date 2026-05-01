/*
 * Copyright 2026 jrosactionlib project
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
package pinorobotics.jros2actionlib.tests.clientexecutors;

import id.jros2client.JRos2Client;
import id.jros2client.JRos2ClientFactory;
import id.xfunction.function.Unchecked;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import pinorobotics.jros2actionlib.JRos2ActionLibFactory;
import pinorobotics.jros2actionlib.tests.ClientExecutor;
import pinorobotics.jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciActionDefinition;
import pinorobotics.jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciGoalMessage;
import pinorobotics.jros2actionlib.tests.actionlib_tutorials_msgs.FibonacciResultMessage;
import pinorobotics.jrosactionlib.JRosActionClient;

/**
 * Uses the *Java* ROS‑2 Action client API to send goals for each distinct {@code order} (seed) and
 * collect the textual output that the test asserts against. It therefore mirrors the behaviour of
 * the external‑process executor but stays inside the JVM.
 *
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class JRos2ActionClientExecutor implements ClientExecutor {

    private final String actionServerName;
    private final int streamSize;
    private List<JRos2Client> jrosClients = new ArrayList<>();
    private List<JRosActionClient<?, ?>> actionClients = new ArrayList<>();

    public JRos2ActionClientExecutor(int streamSize, String actionServerName) {
        this.streamSize = streamSize;
        this.actionServerName = actionServerName;
    }

    @Override
    public List<Future<ClientExecutor.Result>> executeClientsAsync() throws Exception {

        /* ---- 1️⃣  generate distinct order values (seeds) ---- */
        List<Integer> seeds =
                new Random()
                        .ints(streamSize, 1, 20)
                        .distinct()
                        .boxed()
                        .collect(Collectors.toList());
        List<Future<Result>> results = new ArrayList<>();
        for (int seed : seeds) {
            /* ---- 2️⃣  create a low‑level ROS2 client ------------------- */
            JRos2Client jrosClient = new JRos2ClientFactory().createClient();

            jrosClients.add(jrosClient);

            /* ---- 3️⃣  create the *action* client --------------------- */
            JRosActionClient<FibonacciGoalMessage, FibonacciResultMessage> actionClient =
                    new JRos2ActionLibFactory()
                            .createClient(
                                    jrosClient, new FibonacciActionDefinition(), actionServerName);

            actionClients.add(actionClient);

            /* ---- 4️⃣  send the async goal and capture its output -------- */
            // Build the goal message that the external binary would have received
            FibonacciGoalMessage goal = new FibonacciGoalMessage().withOrder(seed);

            // The action client API used by the original test was a *synchronous*
            // call; for the async version we expose a CompletableFuture via the
            // handler's contract.  Here we simply reuse the same `executeClientsAsync`
            // contract from the service‑test: we wrap the result string in a Future.
            CompletableFuture<Result> resultFuture =
                    actionClient
                            .sendGoalAsync(goal)
                            .thenApply(
                                    result ->
                                            String.format(
                                                    """
                                                    : order=%d
                                                    : Sending goal
                                                    : Goal accepted by server, waiting for result
                                                    : Result received: %s\
                                                    """,
                                                    seed,
                                                    Arrays.stream(result.sequence)
                                                            .mapToObj(i -> "" + i)
                                                            .collect(Collectors.joining(" "))))
                            .thenApply(output -> new ClientExecutor.Result(seed, output));

            results.add(resultFuture);
        }

        return results;
    }

    @Override
    public void close() {
        actionClients.forEach(Unchecked.wrapAccept(JRosActionClient::close));
        jrosClients.forEach(JRos2Client::close);
    }
}
