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

package org.kaaproject.kaa.server.common.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.kaaproject.kaa.server.common.dao.EndpointNotificationDao;
import org.kaaproject.kaa.server.common.dao.mongo.model.EndpointNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class EndpointNotificationMongoDao extends AbstractMongoDao<EndpointNotification> implements EndpointNotificationDao<EndpointNotification> {

    private static final Logger LOG = LoggerFactory.getLogger(EndpointNotificationMongoDao.class);

    private static final String ENDPOINT_KEY_HASH = "endpoint_key_hash";
    public static final String APPLICATION_ID = "notification.application_id";

    @Override
    protected String getCollectionName() {
        return EndpointNotification.COLLECTION_NAME;
    }

    @Override
    protected Class<EndpointNotification> getDocumentClass() {
        return EndpointNotification.class;
    }

    // These methods use mongo template directly because we had problems with bytes array.
    @Override
    public List<EndpointNotification> findNotificationsByKeyHash(final byte[] keyHash) {
        LOG.debug("Find unicast notifications by endpoint key hash [{}] ", keyHash);
        DBObject dbObject = query(where(ENDPOINT_KEY_HASH).is(keyHash)).getQueryObject();
        DBCursor cursor = mongoTemplate.getDb().getCollection(EndpointNotification.COLLECTION_NAME).find(dbObject);
        List<EndpointNotification> endpointNotifications = new ArrayList<>();
        while (cursor.hasNext()) {
            endpointNotifications.add(mongoTemplate.getConverter().read(EndpointNotification.class, cursor.next()));
        }
        return endpointNotifications;
    }

    @Override
    public void removeNotificationsByKeyHash(final byte[] keyHash) {
        LOG.debug("Remove unicast notifications by endpoint key hash [{}] ", keyHash);
        mongoTemplate.remove(query(where(ENDPOINT_KEY_HASH).is(keyHash)), getCollectionName());
    }

    @Override
    public void removeNotificationsByAppId(final String appId) {
        LOG.debug("Remove unicast notifications by application id [{}] ", appId);
        remove(query(where(APPLICATION_ID).is(new ObjectId(appId))));
    }
}