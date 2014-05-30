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

package org.kaaproject.kaa.common.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static org.kaaproject.kaa.common.dto.Util.getArrayCopy;

public class EndpointProfileDto implements HasId, Serializable {

    private static final long serialVersionUID = -4124431119223385565L;

    private String id;
    private String applicationId;
    private byte[] endpointKey;
    private byte[] endpointKeyHash;
    private String profileSchemaId;
    private List<EndpointGroupStateDto> endpointGroupState;
    private List<String> subscriptions;
    private byte[] ntHash;
    private int sequenceNumber;
    private Boolean changedFlag;
    private String profile;
    private byte[] profileHash;
    private int profileVersion;
    private byte[] configurationHash;
    private int configurationVersion;
    private int notificationVersion;
    private int systemNfVersion;
    private int userNfVersion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public byte[] getEndpointKey() {
        return endpointKey;
    }

    public void setEndpointKey(byte[] endpointKey) {
        this.endpointKey = getArrayCopy(endpointKey);
    }

    public byte[] getEndpointKeyHash() {
        return endpointKeyHash;
    }

    public void setEndpointKeyHash(byte[] endpointKeyHash) {
        this.endpointKeyHash = getArrayCopy(endpointKeyHash);
    }

    public String getProfileSchemaId() {
        return profileSchemaId;
    }

    public void setProfileSchemaId(String profileSchemaId) {
        this.profileSchemaId = profileSchemaId;
    }

    public List<EndpointGroupStateDto> getEndpointGroups() {
        return endpointGroupState;
    }

    public void setEndpointGroups(List<EndpointGroupStateDto> endpointGroupId) {
        this.endpointGroupState = endpointGroupId;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Boolean getChangedFlag() {
        return changedFlag;
    }

    public void setChangedFlag(Boolean changedFlag) {
        this.changedFlag = changedFlag;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public byte[] getProfileHash() {
        return profileHash;
    }

    public void setProfileHash(byte[] profileHash) {
        this.profileHash = getArrayCopy(profileHash);
    }

    public int getProfileVersion() {
        return profileVersion;
    }

    public void setProfileVersion(int profileVersion) {
        this.profileVersion = profileVersion;
    }

    public byte[] getConfigurationHash() {
        return configurationHash;
    }

    public void setConfigurationHash(byte[] configurationHash) {
        this.configurationHash = getArrayCopy(configurationHash);
    }

    public int getConfigurationVersion() {
        return configurationVersion;
    }

    public void setConfigurationVersion(int configurationVersion) {
        this.configurationVersion = configurationVersion;
    }

    public List<EndpointGroupStateDto> getEndpointGroupState() {
        return endpointGroupState;
    }

    public void setEndpointGroupState(List<EndpointGroupStateDto> endpointGroupState) {
        this.endpointGroupState = endpointGroupState;
    }

    public List<String> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public int getNotificationVersion() {
        return notificationVersion;
    }

    public void setNotificationVersion(int notificationVersion) {
        this.notificationVersion = notificationVersion;
    }

    public byte[] getNtHash() {
        return ntHash;
    }

    public void setNtHash(byte[] ntHash) {
        this.ntHash = getArrayCopy(ntHash);
    }

    public int getSystemNfVersion() {
        return systemNfVersion;
    }

    public void setSystemNfVersion(int systemNfVersion) {
        this.systemNfVersion = systemNfVersion;
    }

    public int getUserNfVersion() {
        return userNfVersion;
    }

    public void setUserNfVersion(int userNfVersion) {
        this.userNfVersion = userNfVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EndpointProfileDto)) {
            return false;
        }

        EndpointProfileDto that = (EndpointProfileDto) o;

        if (configurationVersion != that.configurationVersion) {
            return false;
        }
        if (notificationVersion != that.notificationVersion) {
            return false;
        }
        if (profileVersion != that.profileVersion) {
            return false;
        }
        if (sequenceNumber != that.sequenceNumber) {
            return false;
        }
        if (systemNfVersion != that.systemNfVersion) {
            return false;
        }
        if (userNfVersion != that.userNfVersion) {
            return false;
        }
        if (applicationId != null ? !applicationId.equals(that.applicationId) : that.applicationId != null) {
            return false;
        }
        if (changedFlag != null ? !changedFlag.equals(that.changedFlag) : that.changedFlag != null) {
            return false;
        }
        if (!Arrays.equals(configurationHash, that.configurationHash)) {
            return false;
        }
        if (endpointGroupState != null ? !endpointGroupState.equals(that.endpointGroupState) : that.endpointGroupState != null) {
            return false;
        }
        if (!Arrays.equals(endpointKey, that.endpointKey)) {
            return false;
        }
        if (!Arrays.equals(endpointKeyHash, that.endpointKeyHash)) {
            return false;
        }
        if (!Arrays.equals(ntHash, that.ntHash)) {
            return false;
        }
        if (profile != null ? !profile.equals(that.profile) : that.profile != null) {
            return false;
        }
        if (!Arrays.equals(profileHash, that.profileHash)) {
            return false;
        }
        if (profileSchemaId != null ? !profileSchemaId.equals(that.profileSchemaId) : that.profileSchemaId != null) {
            return false;
        }
        if (subscriptions != null ? !subscriptions.equals(that.subscriptions) : that.subscriptions != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = applicationId != null ? applicationId.hashCode() : 0;
        result = 31 * result + (endpointKey != null ? Arrays.hashCode(endpointKey) : 0);
        result = 31 * result + (endpointKeyHash != null ? Arrays.hashCode(endpointKeyHash) : 0);
        result = 31 * result + (profileSchemaId != null ? profileSchemaId.hashCode() : 0);
        result = 31 * result + (endpointGroupState != null ? endpointGroupState.hashCode() : 0);
        result = 31 * result + (subscriptions != null ? subscriptions.hashCode() : 0);
        result = 31 * result + (ntHash != null ? Arrays.hashCode(ntHash) : 0);
        result = 31 * result + sequenceNumber;
        result = 31 * result + (changedFlag != null ? changedFlag.hashCode() : 0);
        result = 31 * result + (profile != null ? profile.hashCode() : 0);
        result = 31 * result + (profileHash != null ? Arrays.hashCode(profileHash) : 0);
        result = 31 * result + profileVersion;
        result = 31 * result + (configurationHash != null ? Arrays.hashCode(configurationHash) : 0);
        result = 31 * result + configurationVersion;
        result = 31 * result + notificationVersion;
        result = 31 * result + systemNfVersion;
        result = 31 * result + userNfVersion;
        return result;
    }

    @Override
    public String toString() {
        return "EndpointProfileDto{" +
                "id='" + id + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", endpointKey=" + Arrays.toString(endpointKey) +
                ", endpointKeyHash=" + Arrays.toString(endpointKeyHash) +
                ", profileSchemaId='" + profileSchemaId + '\'' +
                ", endpointGroupState=" + endpointGroupState +
                ", subscriptions=" + subscriptions +
                ", ntHash=" + Arrays.toString(ntHash) +
                ", sequenceNumber=" + sequenceNumber +
                ", changedFlag=" + changedFlag +
                ", profile='" + profile + '\'' +
                ", profileHash=" + Arrays.toString(profileHash) +
                ", profileVersion=" + profileVersion +
                ", configurationHash=" + Arrays.toString(configurationHash) +
                ", configurationVersion=" + configurationVersion +
                ", notificationVersion=" + notificationVersion +
                ", systemNfVersion=" + systemNfVersion +
                ", userNfVersion=" + userNfVersion +
                '}';
    }
}