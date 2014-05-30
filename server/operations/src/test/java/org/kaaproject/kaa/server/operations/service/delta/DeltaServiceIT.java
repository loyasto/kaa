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

package org.kaaproject.kaa.server.operations.service.delta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.kaaproject.kaa.server.common.dao.DaoUtil.idToObjectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericRecord;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaaproject.kaa.common.avro.GenericAvroConverter;
import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.common.dto.ConfigurationDto;
import org.kaaproject.kaa.common.dto.EndpointGroupStateDto;
import org.kaaproject.kaa.common.dto.ProfileFilterDto;
import org.kaaproject.kaa.common.dto.ProfileSchemaDto;
import org.kaaproject.kaa.common.endpoint.gen.BasicEndpointProfile;
import org.kaaproject.kaa.common.hash.EndpointObjectHash;
import org.kaaproject.kaa.server.common.dao.ApplicationService;
import org.kaaproject.kaa.server.common.dao.ConfigurationDao;
import org.kaaproject.kaa.server.common.dao.ConfigurationSchemaDao;
import org.kaaproject.kaa.server.common.dao.ConfigurationService;
import org.kaaproject.kaa.server.common.dao.EndpointConfigurationDao;
import org.kaaproject.kaa.server.common.dao.EndpointGroupDao;
import org.kaaproject.kaa.server.common.dao.EndpointProfileDao;
import org.kaaproject.kaa.server.common.dao.ProfileFilterDao;
import org.kaaproject.kaa.server.common.dao.ProfileSchemaDao;
import org.kaaproject.kaa.server.common.dao.ProfileService;
import org.kaaproject.kaa.server.common.dao.TenantDao;
import org.kaaproject.kaa.server.common.dao.exception.IncorrectParameterException;
import org.kaaproject.kaa.server.common.dao.mongo.model.Configuration;
import org.kaaproject.kaa.server.common.dao.mongo.model.ConfigurationSchema;
import org.kaaproject.kaa.server.common.dao.mongo.model.EndpointConfiguration;
import org.kaaproject.kaa.server.common.dao.mongo.model.EndpointGroup;
import org.kaaproject.kaa.server.common.dao.mongo.model.EndpointGroupState;
import org.kaaproject.kaa.server.common.dao.mongo.model.EndpointProfile;
import org.kaaproject.kaa.server.common.dao.mongo.model.ProfileFilter;
import org.kaaproject.kaa.server.common.dao.mongo.model.ProfileSchema;
import org.kaaproject.kaa.server.common.dao.mongo.model.Tenant;
import org.kaaproject.kaa.server.common.dao.schema.SchemaCreationException;
import org.kaaproject.kaa.server.common.dao.mongo.MongoDBTestRunner;
import org.kaaproject.kaa.server.operations.pojo.GetDeltaRequest;
import org.kaaproject.kaa.server.operations.pojo.GetDeltaResponse;
import org.kaaproject.kaa.server.operations.service.OperationsServiceIT;
import org.kaaproject.kaa.server.operations.service.cache.CacheService;
import org.kaaproject.kaa.server.operations.service.delta.DeltaCalculatorException;
import org.kaaproject.kaa.server.operations.service.delta.DeltaService;
import org.kaaproject.kaa.server.operations.service.delta.HistoryDelta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/common-test-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class DeltaServiceIT {
    protected static final Logger LOG = LoggerFactory.getLogger(DeltaServiceIT.class);

    private static final int PROFILE_VERSION = 1;
    private static final int PROFILE_SCHEMA_VERSION = 1;
    private static final int OLD_ENDPOINT_SEQ_NUMBER = 0;
    private static final int NEW_APPLICATION_SEQ_NUMBER = 6;
    private static final int MAJOR_VERSION = 1;

    private static final int CONF_SCHEMA_VERSION = 2;

    private static final String CUSTOMER_ID = "CustomerId";
    private static final String APPLICATION_ID = "ApplicationId";
    private static final String APPLICATION_NAME = "ApplicationName";
    private static final GenericAvroConverter<GenericRecord> avroConverter = new GenericAvroConverter<GenericRecord>(BasicEndpointProfile.SCHEMA$);
    private static final BasicEndpointProfile ENDPOINT_PROFILE = new BasicEndpointProfile("dummy profile 1");
    private static byte[] PROFILE_BYTES;
    private static String PROFILE_JSON;

    private static final byte[] ENDPOINT_KEY = "EndpointKey".getBytes();

    @Autowired
    protected DeltaService deltaService;

    @Autowired
    protected TenantDao<Tenant> customerDao;

    @Autowired
    protected ApplicationService applicationService;

    @Autowired
    protected ConfigurationSchemaDao<ConfigurationSchema> configurationSchemaDao;

    @Autowired
    protected ConfigurationService configurationService;

    @Autowired
    protected ConfigurationDao<Configuration> configurationDao;

    @Autowired
    protected EndpointConfigurationDao<EndpointConfiguration> endpointConfigurationDao;

    @Autowired
    protected EndpointProfileDao<EndpointProfile> endpointProfileDao;

    @Autowired
    protected ProfileSchemaDao<ProfileSchema> profileSchemaDao;

    @Autowired
    protected EndpointGroupDao<EndpointGroup> endpointGroupDao;

    @Autowired
    protected ProfileFilterDao<ProfileFilter> profileFilterDao;

    @Autowired
    protected ProfileService profileService;

    @Autowired
    protected CacheService cacheService;

    @Autowired
    protected ConfigurationService confService;

    private Tenant customerDto;
    private ApplicationDto app;
    private ProfileSchemaDto profileSchema;
    private ProfileFilterDto profileFilter;
    private EndpointProfile endpointProfile;
    private EndpointConfiguration endpointConfiguration;
    private byte[] endpointConfigurationBytes;
    private ConfigurationSchema confSchema;

    private String egAllId;
    private String pfAllId;
    private String cfAllId;


    @BeforeClass
    public static void init() throws Exception {
        MongoDBTestRunner.setUp();
    }

    @AfterClass
    public static void after() throws Exception {
        MongoDBTestRunner.getDB().dropDatabase();
        MongoDBTestRunner.tearDown();
    }

    @Before
    public void beforeTest() throws IOException, SchemaCreationException, DeltaCalculatorException {
        String dataSchema = OperationsServiceIT.getResourceAsString(OperationsServiceIT.DATA_SCHEMA_LOCATION);
        PROFILE_BYTES = avroConverter.encode(ENDPOINT_PROFILE);
        PROFILE_JSON = avroConverter.endcodeToJson(ENDPOINT_PROFILE);

        customerDto = new Tenant();
        customerDto.setName(CUSTOMER_ID);
        customerDto = customerDao.save(customerDto);
        assertNotNull(customerDto);
        assertNotNull(customerDto.getId());

        app = new ApplicationDto();
        app.setTenantId(customerDto.getId());
        app.setApplicationToken(APPLICATION_ID);
        app.setName(APPLICATION_NAME);
        app.setSequenceNumber(NEW_APPLICATION_SEQ_NUMBER);
        app = applicationService.saveApp(app);
        assertNotNull(app);
        assertNotNull(app.getId());

        EndpointGroup groupAll = endpointGroupDao.findByAppIdAndWeight(app.getId(), 0);

        ProfileSchema profileSchemaObj = new ProfileSchema();
        profileSchemaObj.setMajorVersion(PROFILE_SCHEMA_VERSION);
        profileSchemaObj.setMinorVersion(0);
        profileSchemaObj.setSchema(BasicEndpointProfile.SCHEMA$.toString());
        profileSchemaObj.setApplicationId(idToObjectId(app.getId()));
        profileSchema = profileService.saveProfileSchema(profileSchemaObj.toDto());

        EndpointGroup endpointGroup = new EndpointGroup();
        endpointGroup.setApplicationId(new ObjectId(app.getId()));
        endpointGroup.setName("Test group");
        endpointGroup.setWeight(277);
        endpointGroup.setDescription("Test Description");
        endpointGroup = endpointGroupDao.save(endpointGroup);

        ProfileFilter profileFilterObj = new ProfileFilter();
        profileFilterObj.setApplicationId(idToObjectId(app.getId()));
        profileFilterObj.setEndpointGroupId(idToObjectId(endpointGroup.getId()));
        profileFilterObj.setBody("profileBody.contains(\"dummy\")");
        profileFilterObj.setSchemaId(idToObjectId(profileSchema.getId()));
        profileFilter = profileService.saveProfileFilter(profileFilterObj.toDto());
        profileService.activateProfileFilter(profileFilter.getId(), null);

        confSchema = new ConfigurationSchema();
        confSchema.setApplicationId(idToObjectId(app.getId()));
        confSchema.setMajorVersion(CONF_SCHEMA_VERSION);
        confSchema.setSchema(dataSchema);
        try {
            confSchema = new ConfigurationSchema(configurationService.saveConfSchema(confSchema.toDto()));
        } catch (IncorrectParameterException e) {
            Assert.fail("Can't generate schemas");
        }
        Assert.assertNotNull(confSchema);
        Assert.assertNotNull(confSchema.getId());

        egAllId = groupAll.getId();
        pfAllId = profileFilter.getId();
        ConfigurationDto confDto = configurationService.findConfigurationByEndpointGroupIdAndVersion(egAllId, CONF_SCHEMA_VERSION);
        cfAllId = confDto.getId();

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setConfiguration(confDto.getBinaryBody());
        endpointConfiguration.setConfigurationHash(EndpointObjectHash.fromSHA1(confDto.getBody()).getData());
        endpointConfiguration = endpointConfigurationDao.save(endpointConfiguration);
        assertNotNull(endpointConfiguration);
        assertNotNull(endpointConfiguration.getId());

        EndpointGroupState egs = new EndpointGroupState();
        egs.setConfigurationId(idToObjectId(cfAllId));
        egs.setEndpointGroupId(idToObjectId(egAllId));
        egs.setProfileFilterId(idToObjectId(pfAllId));

        endpointProfile = new EndpointProfile();
        endpointProfile.setProfile((DBObject) JSON.parse(PROFILE_JSON));
        endpointProfile.setProfileHash(EndpointObjectHash.fromSHA1(PROFILE_BYTES).getData());
        endpointProfile.setConfigurationHash(endpointConfiguration.getConfigurationHash());
        endpointProfile.setConfigurationVersion(MAJOR_VERSION);
        endpointProfile.setProfileVersion(PROFILE_VERSION);
        endpointProfile.setEndpointGroup(Collections.singletonList(egs));
        endpointProfile = endpointProfileDao.save(endpointProfile);
        assertNotNull(endpointProfile);
        assertNotNull(endpointProfile.getId());
    }

    @After
    public void afterTest() {
        MongoDBTestRunner.getDB().dropDatabase();
    }

    @Test
    public void testDeltaServiceSameSeqNumbers() throws Exception {
        GetDeltaRequest request = new GetDeltaRequest(app.getApplicationToken(), EndpointObjectHash.fromSHA1(ENDPOINT_KEY),
                EndpointObjectHash.fromSHA1(PROFILE_BYTES), EndpointObjectHash.fromSHA1(endpointConfiguration.getConfiguration()), OLD_ENDPOINT_SEQ_NUMBER);
        GetDeltaResponse response = deltaService.getDelta(request, new HistoryDelta(), OLD_ENDPOINT_SEQ_NUMBER);
        assertNotNull(response);
        assertEquals(GetDeltaResponse.GetDeltaResponseType.NO_DELTA, response.getResponseType());
        assertEquals(OLD_ENDPOINT_SEQ_NUMBER, response.getSequenceNumber());
        assertNull(response.getDelta());
        assertNull(response.getConfSchema());
    }

    @Test
    public void testDeltaServiceNoHistoryDelta() throws Exception {
        GetDeltaRequest request = new GetDeltaRequest(app.getApplicationToken(), EndpointObjectHash.fromSHA1(ENDPOINT_KEY),
                EndpointObjectHash.fromSHA1(PROFILE_BYTES), EndpointObjectHash.fromSHA1(endpointConfiguration.getConfiguration()), OLD_ENDPOINT_SEQ_NUMBER);
        request.setEndpointProfile(endpointProfile.toDto());
        HistoryDelta historyDelta = new HistoryDelta(new ArrayList<EndpointGroupStateDto>(), false, false);
        GetDeltaResponse response = deltaService.getDelta(request, historyDelta, NEW_APPLICATION_SEQ_NUMBER);
        assertNotNull(response);
        assertEquals(GetDeltaResponse.GetDeltaResponseType.NO_DELTA, response.getResponseType());
        assertEquals(NEW_APPLICATION_SEQ_NUMBER, response.getSequenceNumber());
        assertNull(response.getDelta());
        assertNull(response.getConfSchema());
    }

    @Test
    @Ignore("Kaa #7786")
    public void testDeltaServiceNoHistoryDeltaFetchSchema() throws Exception {
        GetDeltaRequest request = new GetDeltaRequest(app.getApplicationToken(), EndpointObjectHash.fromSHA1(ENDPOINT_KEY),
                EndpointObjectHash.fromSHA1(PROFILE_BYTES), EndpointObjectHash.fromSHA1(endpointConfiguration.getConfiguration()), OLD_ENDPOINT_SEQ_NUMBER);
        request.setEndpointProfile(endpointProfile.toDto());
        request.setFetchSchema(true);
        HistoryDelta historyDelta = new HistoryDelta(new ArrayList<EndpointGroupStateDto>(), false, false);
        GetDeltaResponse response = deltaService.getDelta(request, historyDelta, NEW_APPLICATION_SEQ_NUMBER);
        assertNotNull(response);
        assertEquals(GetDeltaResponse.GetDeltaResponseType.NO_DELTA, response.getResponseType());
        assertEquals(NEW_APPLICATION_SEQ_NUMBER, response.getSequenceNumber());
        assertNull(response.getDelta());
        assertNotNull(response.getConfSchema());
    }

    @Test
    public void testDeltaServiceFirstRequest() throws Exception {
        GetDeltaRequest request = new GetDeltaRequest(app.getApplicationToken(), EndpointObjectHash.fromSHA1(ENDPOINT_KEY),
                EndpointObjectHash.fromSHA1(PROFILE_BYTES), null, OLD_ENDPOINT_SEQ_NUMBER);
        request.setEndpointProfile(endpointProfile.toDto());
        List<EndpointGroupStateDto> changes = new ArrayList<>();
        changes.add(new EndpointGroupStateDto(egAllId, pfAllId, cfAllId));
        HistoryDelta historyDelta = new HistoryDelta(changes, true, false);
        GetDeltaResponse response = deltaService.getDelta(request, historyDelta, NEW_APPLICATION_SEQ_NUMBER);

        assertNotNull(response);
        assertEquals(GetDeltaResponse.GetDeltaResponseType.CONF_RESYNC, response.getResponseType());
        assertEquals(NEW_APPLICATION_SEQ_NUMBER, response.getSequenceNumber());
        assertNotNull(response.getDelta());
        endpointConfigurationBytes = response.getDelta().getData();
        assertNotNull(endpointConfigurationBytes);
    }

    @Test
    public void testDeltaServiceHashMismatch() throws Exception {
        byte[] wrongConf = Arrays.copyOf(endpointConfiguration.getConfiguration(), endpointConfiguration.getConfiguration().length);
        wrongConf[0] = (byte)(wrongConf[0]+1);
        EndpointObjectHash newConfHash = EndpointObjectHash.fromSHA1(wrongConf);
        GetDeltaRequest request = new GetDeltaRequest(app.getApplicationToken(), EndpointObjectHash.fromSHA1(ENDPOINT_KEY),
                EndpointObjectHash.fromSHA1(PROFILE_BYTES), newConfHash, OLD_ENDPOINT_SEQ_NUMBER);

        request.setEndpointProfile(endpointProfile.toDto());
        List<EndpointGroupStateDto> changes = new ArrayList<>();
        changes.add(new EndpointGroupStateDto(egAllId, pfAllId, cfAllId));
        HistoryDelta historyDelta = new HistoryDelta(changes, true, false);
        GetDeltaResponse response = deltaService.getDelta(request, historyDelta, NEW_APPLICATION_SEQ_NUMBER);
        assertNotNull(response);
        assertEquals(GetDeltaResponse.GetDeltaResponseType.CONF_RESYNC, response.getResponseType());
        assertEquals(NEW_APPLICATION_SEQ_NUMBER, response.getSequenceNumber());
        assertNotNull(response.getDelta());
        assertNotNull(response.getDelta().getData());
    }

    @Test
    public void testDeltaServiceSecondRequest() throws Exception {
        GenericAvroConverter<GenericContainer> newConfConverter = new GenericAvroConverter<>(new Schema.Parser().parse(confSchema.getBaseSchema()));
        GenericContainer container = newConfConverter.decodeJson(OperationsServiceIT.getResourceAsString(OperationsServiceIT.BASE_DATA_UPDATED_LOCATION));
        byte[] newConfData = newConfConverter.encodeToJsonBytes(container);

        ConfigurationDto newConfDto = new ConfigurationDto();
        newConfDto.setEndpointGroupId(egAllId);
        newConfDto.setSchemaId(confSchema.getId());
        newConfDto.setBinaryBody(newConfData);

        newConfDto = configurationService.saveConfiguration(newConfDto);
        configurationService.activateConfiguration(newConfDto.getId(), confSchema.getCreatedUsername());

        GetDeltaRequest request = new GetDeltaRequest(app.getApplicationToken(), EndpointObjectHash.fromSHA1(ENDPOINT_KEY),
                EndpointObjectHash.fromSHA1(PROFILE_BYTES), EndpointObjectHash.fromSHA1(endpointConfiguration.getConfiguration()), OLD_ENDPOINT_SEQ_NUMBER);

        request.setEndpointProfile(endpointProfile.toDto());
        List<EndpointGroupStateDto> changes = new ArrayList<>();
        changes.add(new EndpointGroupStateDto(egAllId, pfAllId, newConfDto.getId()));
        HistoryDelta historyDelta = new HistoryDelta(changes, true, false);
        GetDeltaResponse response = deltaService.getDelta(request, historyDelta, NEW_APPLICATION_SEQ_NUMBER);
        assertNotNull(response);
        assertEquals(GetDeltaResponse.GetDeltaResponseType.DELTA, response.getResponseType());
        assertEquals(NEW_APPLICATION_SEQ_NUMBER, response.getSequenceNumber());
        assertNotNull(response.getDelta());
        assertNotNull(response.getDelta().getData());
    }

    @Test
    public void testDeltaServiceSecondRequestNoChanges() throws Exception {
        GetDeltaRequest request = new GetDeltaRequest(app.getApplicationToken(), EndpointObjectHash.fromSHA1(ENDPOINT_KEY),
                EndpointObjectHash.fromSHA1(PROFILE_BYTES), EndpointObjectHash.fromSHA1(endpointConfiguration.getConfiguration()), OLD_ENDPOINT_SEQ_NUMBER);
        endpointProfile.setConfigurationHash(EndpointObjectHash.fromSHA1(endpointConfiguration.getConfiguration()).getData());
        request.setEndpointProfile(endpointProfile.toDto());
        List<EndpointGroupStateDto> changes = new ArrayList<>();
        changes.add(new EndpointGroupStateDto(egAllId, pfAllId, cfAllId));
        HistoryDelta historyDelta = new HistoryDelta(changes, true, false);
        GetDeltaResponse response = deltaService.getDelta(request, historyDelta, NEW_APPLICATION_SEQ_NUMBER);
        assertNotNull(response);
        assertEquals(GetDeltaResponse.GetDeltaResponseType.NO_DELTA, response.getResponseType());
        assertEquals(NEW_APPLICATION_SEQ_NUMBER, response.getSequenceNumber());
        assertNull(response.getDelta());
    }
}