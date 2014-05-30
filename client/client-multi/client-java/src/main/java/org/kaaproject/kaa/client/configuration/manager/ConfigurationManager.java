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

package org.kaaproject.kaa.client.configuration.manager;

import org.kaaproject.kaa.client.common.CommonRecord;

/**
 * Interface for configuration manager.
 *
 * Responsible for configuration updates subscriptions and configuration obtaining.
 *
 * @author Yaroslav Zeygerman
 *
 */
public interface ConfigurationManager {

    /**
     * Subscribes for configuration updates
     *
     * @param receiver object which is going to receive updates
     *
     */
    void subscribeForConfigurationUpdates(ConfigurationReceiver receiver);

    /**
     * Unsubscribes from configuration updates
     *
     * @param receiver object which is no longer needs configuration updates
     *
     */
    void unsubscribeFromConfigurationUpdates(ConfigurationReceiver receiver);

    /**
     * Retrieves full configuration
     *
     * @return common object with full configuration
     *
     */
    CommonRecord getConfiguration();

}