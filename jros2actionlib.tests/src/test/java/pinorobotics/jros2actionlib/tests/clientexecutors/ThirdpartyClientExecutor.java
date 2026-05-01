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

import id.xfunction.lang.XExec;
import id.xfunction.lang.XProcess;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import pinorobotics.jros2actionlib.tests.ClientExecutor;

/**
 * Executes the **binary** ROS‑2 action client for a given order. Captures its output into a
 * temporary file and returns the captured output wrapped in a {@link Future}.
 *
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class ThirdpartyClientExecutor implements ClientExecutor {

    private final int streamSize;
    private final List<XProcess> procs = new ArrayList<>();
    private Path clientLogs;

    public ThirdpartyClientExecutor(int streamSize) {
        this.streamSize = streamSize;
    }

    @Override
    public List<Future<ClientExecutor.Result>> executeClientsAsync() throws Exception {
        /* ---- 1.  generate distinct seed values (orders) ---- */
        List<Integer> seeds =
                new Random()
                        .ints(streamSize, 1, 20)
                        .distinct()
                        .boxed()
                        .collect(Collectors.toList());

        clientLogs = Files.createTempDirectory("actionlib-test");

        List<Future<ClientExecutor.Result>> futures = new ArrayList<>();

        /* ---- 2.  launch a process for each seed ------------------- */
        for (int seed : seeds) {
            XProcess proc =
                    new XExec(
                                    "bash",
                                    "-c",
"""
cd ws2/out.%s &&
COLCON_CURRENT_PREFIX=./install source install/setup.sh &&
build/action_tutorials_cpp/fibonacci_action_client --ros-args --log-level DEBUG -p order:=%d
"""
                                            .formatted(System.getenv("ROS_DISTRO"), seed))
                            .start()
                            .stderrAsync(newLogger(clientLogs, seed));

            /* ---- 3.  when the process ends, read the log and return -- */
            CompletableFuture<Result> future =
                    proc.code()
                            .thenAccept(
                                    exitCode -> {
                                        if (exitCode != 0) {
                                            throw new RuntimeException(
                                                    "process exited with code " + exitCode);
                                        }
                                    })
                            .thenApply(empty -> createResult(seed));
            futures.add(future);
        }
        return futures;
    }

    @Override
    public void close() {
        procs.forEach(XProcess::destroyAllForcibly);
    }

    private Result createResult(int seed) {
        Path logFile = clientLogs.resolve("" + seed);
        try {
            String actualOutput =
                    Files.lines(logFile)
                            .filter(l -> l.startsWith("[INFO]"))
                            .filter(l -> !l.contains("waiting for service to appear"))
                            .map(l -> l.replaceAll("\\[.*\\]", "").strip())
                            .collect(Collectors.joining("\n"));
            return new Result(seed, actualOutput);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
}
