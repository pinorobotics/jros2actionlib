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
package pinorobotics.jros2actionlib.tests;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author lambdaprime intid@protonmail.com
 */
public interface ClientExecutor extends AutoCloseable {

    record Result(int seed, String output) {}

    /**
     * Executes client processes asynchronously
     *
     * @return a list of {@link Future}s, each completing with the captured result
     */
    List<Future<Result>> executeClientsAsync() throws Exception;

    @Override
    void close();
}
