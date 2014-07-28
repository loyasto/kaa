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

#include "kaa/KaaDefaults.hpp"

namespace kaa {

const char * const APPLICATION_TOKEN = "999739850901241";

const boost::uint32_t PROFILE_VERSION = 1;

const boost::uint32_t CONFIG_VERSION = 1;

const boost::uint32_t SYSTEM_NF_VERSION = 1;

const boost::uint32_t USER_NF_VERSION = 1;

const boost::uint32_t LOG_SCHEMA_VERSION = 1;

const boost::uint32_t POLLING_PERIOD_SECONDS = 5;

const char * const CLIENT_PUB_KEY_LOCATION = "key.public";

const char * const CLIENT_PRIV_KEY_LOCATION = "key.private";

const char * const CLIENT_STATUS_FILE_LOCATION = "kaa.status";

const BootstrapServers& getServerInfoList() {
    /* Default value for unit test */
    static const BootstrapServers listOfServers = { {"test.com:443", {"public", 6}}, {"hello.ua:54", {"key", 3}} };
    return listOfServers;
}

const Botan::SecureVector<boost::uint8_t>& getDefaultConfigData() {
    static const Botan::SecureVector<boost::uint8_t> configData = Botan::base64_decode("");
    return configData;
}

const std::string& getDefaultConfigSchema() {
    static const std::string configSchema = "";
    return configSchema;
}

const EventClassFamilyVersionInfos& getEventClassFamilyVersionInfo() {
    static const EventClassFamilyVersionInfos versions;/* = {{"familyName1",1}, {"familyName2",3}};*/
    return versions;
}

}

