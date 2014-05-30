/*
 * Copyright 2014 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaaproject.kaa.server.common.http.server;

/**
 *
 * Netty Server configuration constants.
 *
 */
public interface ConfigConst {

    /**
     * Default value of ServerInitializer Class.
     */
    String DEFAULT_SERVER_INITIALIZER_CLASS = "org.kaaproject.kaa.server.common.http.server.DefaultServerInitializer";

    /**
     * Default HTTP server bind port.
     */
    int DEFAULT_PORT = 9998;

    /**
     * Default HTTP server bind interface, if set "any" bind on all interfaces.
     */
    String DEFAULT_BIND_INTERFACE = "any";

    /**
     * Default Executor thread size.
     */
    int DEFAULT_EXECUTOR_THREAD_SIZE = 3;

    /**
     * Default value of Maximum size of HTTP request body.
     */
    int DEFAULT_MAX_SIZE_VALUE = 10240;

    /**
     * UUID String constant, used as key in Channel context
     */
    String UUID_KEY = "UUID";
    String TRACK_KEY = "TRACK";
}