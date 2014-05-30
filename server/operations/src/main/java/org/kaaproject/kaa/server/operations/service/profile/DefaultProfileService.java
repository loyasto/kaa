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

package org.kaaproject.kaa.server.operations.service.profile;

import java.util.ArrayList;
import java.util.List;

import org.kaaproject.kaa.common.avro.GenericAvroConverter;
import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.common.dto.EndpointGroupStateDto;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;
import org.kaaproject.kaa.common.dto.ProfileSchemaDto;
import org.kaaproject.kaa.common.hash.EndpointObjectHash;
import org.kaaproject.kaa.server.common.dao.ApplicationService;
import org.kaaproject.kaa.server.common.dao.EndpointService;
import org.kaaproject.kaa.server.operations.pojo.RegisterProfileRequest;
import org.kaaproject.kaa.server.operations.pojo.UpdateProfileRequest;
import org.kaaproject.kaa.server.operations.service.filter.FilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class DefaultProfileService is a default implementation of
 * {@link ProfileService ProfileService}.
 * 
 * @author ashvayka
 */
public class DefaultProfileService implements ProfileService {

    /** The LOG constant. */
    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultProfileService.class);

    /** The application service. */
    @Autowired
    private ApplicationService applicationService;

    /** The endpoint service. */
    @Autowired
    private EndpointService endpointService;

    /** The profile service. */
    @Autowired
    private org.kaaproject.kaa.server.common.dao.ProfileService profileService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.kaaproject.kaa.server.operations.service.profile.ProfileService#getProfile
     * (org.kaaproject.kaa.common.hash.EndpointObjectHash)
     */
    @Override
    public EndpointProfileDto getProfile(EndpointObjectHash endpointKey) {
        return endpointService.findEndpointProfileByKeyHash(endpointKey.getData());
    }

    @Override
    public EndpointProfileDto updateProfile(EndpointProfileDto profile) {
        LOG.debug("Updating profile {} ", profile);
        return endpointService.saveEndpointProfile(profile);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kaaproject.kaa.server.operations.service.profile.ProfileService#
     * registerProfile
     * (org.kaaproject.kaa.server.operations.pojo.RegisterProfileRequest)
     */
    @Override
    public EndpointProfileDto registerProfile(RegisterProfileRequest request) {
        LOG.debug("Registering Profile for {}", request.getEndpointKey());
        LOG.trace("Lookup application by token: {}", request.getAppToken());

        ApplicationDto applicationDto = applicationService.findAppByApplicationToken(request.getAppToken());
        LOG.trace("Application by token: {} found: {}", request.getAppToken(), applicationDto);

        String profileJson = decodeProfile(request.getProfile(), applicationDto.getId(), request.getVersionInfo().getProfileVersion());

        EndpointProfileDto dto = new EndpointProfileDto();
        dto.setApplicationId(applicationDto.getId());
        dto.setEndpointKey(request.getEndpointKey());
        dto.setEndpointKeyHash(EndpointObjectHash.fromSHA1(request.getEndpointKey()).getData());
        dto.setProfile(profileJson);
        dto.setProfileHash(EndpointObjectHash.fromSHA1(request.getProfile()).getData());
        dto.setProfileVersion(request.getVersionInfo().getProfileVersion());
        dto.setConfigurationVersion(request.getVersionInfo().getConfigVersion());
        dto.setSystemNfVersion(request.getVersionInfo().getSystemNfVersion());
        dto.setUserNfVersion(request.getVersionInfo().getUserNfVersion());
        dto.setSequenceNumber(0);
        dto.setChangedFlag(Boolean.FALSE);

        return endpointService.saveEndpointProfile(dto);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kaaproject.kaa.server.operations.service.profile.ProfileService#
     * updateProfile
     * (org.kaaproject.kaa.server.operations.pojo.UpdateProfileRequest)
     */
    @Override
    public EndpointProfileDto updateProfile(UpdateProfileRequest request) {
        LOG.debug("Updating Profile for {}", request.getEndpointKeyHash());

        EndpointProfileDto dto = endpointService.findEndpointProfileByKeyHash(request.getEndpointKeyHash().getData());

        String profileJson = decodeProfile(request.getProfile(), dto.getApplicationId(), request.getVersionInfo().getProfileVersion());

        dto.setProfile(profileJson);
        dto.setProfileHash(EndpointObjectHash.fromSHA1(request.getProfile()).getData());
        dto.setProfileVersion(request.getVersionInfo().getProfileVersion());
        dto.setConfigurationVersion(request.getVersionInfo().getConfigVersion());
        dto.setSystemNfVersion(request.getVersionInfo().getSystemNfVersion());
        dto.setUserNfVersion(request.getVersionInfo().getUserNfVersion());


        List<EndpointGroupStateDto> egsList = new ArrayList<>();
        dto.setEndpointGroups(egsList);
        dto.setSequenceNumber(0);
        return endpointService.saveEndpointProfile(dto);
    }

    /**
     * Decode profile.
     * 
     * @param profileRaw
     *            the profile raw
     * @param appId
     *            the app id
     * @param schemaVersion
     *            the schema version
     * @return the string
     */
    private String decodeProfile(byte[] profileRaw, String appId, int schemaVersion) {
        LOG.trace("Lookup profileSchema by appId: {} and version: {}", appId, schemaVersion);

        ProfileSchemaDto profileSchemaDto = profileService.findProfileSchemaByAppIdAndVersion(appId, schemaVersion);
        String profileSchema = profileSchemaDto.getSchema();

        LOG.trace("ProfileSchema by appId: {} and version: {} found: {}", appId, schemaVersion, profileSchema);

        String profileJson = GenericAvroConverter.toJson(profileRaw, profileSchema);
        LOG.trace("Profile json : {} ", profileJson);

        return profileJson;
    }
}