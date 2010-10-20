/*
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.syncope.console.rest;

import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.HttpServerErrorException;
import org.syncope.client.mod.UserMod;
import org.syncope.client.to.ConfigurationTO;
import org.syncope.client.search.NodeCond;
import org.syncope.client.to.UserTO;
import org.syncope.client.validation.SyncopeClientCompositeErrorException;
import org.syncope.console.commons.Constants;

/**
 * Console client for invoking rest users services.
 */
public class UsersRestClient {

    protected RestClient restClient;

    public List<UserTO> getAllUsers() {
        List<UserTO> users = null;
        try {
            users = Arrays.asList(restClient.getRestTemplate().getForObject(
                    restClient.getBaseURL() + "user/list.json", UserTO[].class));
        } catch (SyncopeClientCompositeErrorException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Create a new user and start off the workflow.
     * @param userTO instance
     */
    public void createUser(UserTO userTO) {
        UserTO newUserTO;

        try {
            // 1. create user
            newUserTO = restClient.getRestTemplate().postForObject(
            restClient.getBaseURL() + "user/create", userTO, UserTO.class);

            //userTO.setId(newUserTO.getId());
            //userTO.setToken(newUserTO.getToken());
            //userTO.setTokenExpireTime(newUserTO.getTokenExpireTime());
            
//            newUserTO = restClient.getRestTemplate().postForObject(
//                    restClient.getBaseURL()
//                    + "user/activate", newUserTO, UserTO.class);

        } catch (SyncopeClientCompositeErrorException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Update existing user.
     * @param userTO
     * @return true is the opertion ends succesfully, false otherwise
     */
    public boolean updateUser(final UserMod userModTO) {
        UserTO newUserTO = null;

        try {

            newUserTO = restClient.getRestTemplate().postForObject(
            restClient.getBaseURL() + "user/update", userModTO, UserTO.class);
        } catch (SyncopeClientCompositeErrorException e) {
            e.printStackTrace();
            throw e;
        }

        return userModTO.getId() == newUserTO.getId();
    }

    public void deleteUser(String id) {
        try {
            restClient.getRestTemplate().delete(restClient.getBaseURL()
            + "user/delete/{userId}", new Integer(id));
        } catch (SyncopeClientCompositeErrorException e) {
            e.printStackTrace();
        }
    }

    public UserTO getUser(String id) {
        UserTO userTO = null;
        try {
            userTO = restClient.getRestTemplate().getForObject(
                    restClient.getBaseURL()
                    + "user/read/{userId}.json",
                    UserTO.class, id);
        } catch (SyncopeClientCompositeErrorException e) {
            e.printStackTrace();
        }
        return userTO;
    }

    /**
     * Create a new configuration.
     * @param configurationTO
     * @return true if the operation ends succesfully, false otherwise
     */
    public boolean createConfigurationAttributes(
            ConfigurationTO configurationTO) {

        ConfigurationTO newConfigurationTO =
                restClient.getRestTemplate().postForObject(
                restClient.getBaseURL() + "configuration/create",
                configurationTO, ConfigurationTO.class);

        return configurationTO.equals(newConfigurationTO);
    }

    /**
     * Update an existent configuration.
     * @param configurationTO
     * @return true if the operation ends succesfully, false otherwise
     */
    public boolean updateConfigurationAttributes(
            ConfigurationTO configurationTO) {

        ConfigurationTO newConfigurationTO =
                restClient.getRestTemplate().postForObject(
                restClient.getBaseURL() + "configuration/update",
                configurationTO, ConfigurationTO.class);

        return configurationTO.equals(newConfigurationTO);
    }

    /**
     * Load an existent configuration.
     * @return ConfigurationTO object if the configuration exists,
     * null otherwise
     */
    public ConfigurationTO readConfigurationDisplayAttributes() {

        ConfigurationTO configurationTO;
        try {
            configurationTO = restClient.getRestTemplate().getForObject(
                    restClient.getBaseURL() + "configuration/read/{confKey}",
                    ConfigurationTO.class,
                    Constants.CONF_USERS_ATTRIBUTES_VIEW);
        } catch (SyncopeClientCompositeErrorException e) {
            e.printStackTrace();
            return null;
        }

        return configurationTO;
    }

    /**
     * Search an user by its schema values.
     * @param userTO
     * @return UserTOs
     */
    public List<UserTO> searchUsers(NodeCond nodeSearchCondition) {
        List<UserTO> matchedUsers = null;

        try {
            matchedUsers = restClient.getRestTemplate().postForObject(
                    restClient.getBaseURL() + "user/search",
                    nodeSearchCondition, List.class);
        } catch (HttpServerErrorException e) {
            e.printStackTrace();
            throw e;
        }

        return matchedUsers;
    }

    public RestClient getRestClient() {
        return restClient;
    }

    public void setRestClient(RestClient restClient) {
        this.restClient = restClient;
    }
}
