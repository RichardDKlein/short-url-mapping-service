/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.dao;

import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

/**
 * The production implementation of the Parameter Store Reader interface.
 */
@Component
public class ParameterStoreReaderImpl implements ParameterStoreReader {
    private static final String SHORT_URL_MAPPING_TABLE_NAME = "/shortUrl/mappings/tableName";

    private final SsmClient ssmClient;

    private String shortUrlMappingTableName;

    /**
     * General constructor.
     *
     * @param ssmClient Dependency injection of a class instance that is to play
     *                  the role of an SSM (Simple Systems Manager) Client.
     */
    public ParameterStoreReaderImpl(SsmClient ssmClient) {
        this.ssmClient = ssmClient;
        loadParameters();
    }

    @Override
    public String getShortUrlMappingTableName() {
        return shortUrlMappingTableName;
    }

    /**
     * Load all parameters from the Parameter Store.
     */
    private void loadParameters() {
        shortUrlMappingTableName = getParameter(SHORT_URL_MAPPING_TABLE_NAME);
    }

    /**
     * Get a parameter from the Parameter Store.
     *
     * @param parameterName The name of the parameter of interest.
     * @return The value of the parameter in the Parameter Store.
     */
    private String getParameter(String parameterName) {
        GetParameterResponse parameterResponse =
                ssmClient.getParameter(req -> req.name(parameterName));
        return parameterResponse.parameter().value();
    }
}
