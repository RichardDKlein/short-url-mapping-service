/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.dao;

/**
 * The Parameter Store Reader interface.
 *
 * <p>Specifies the methods that must be implemented by any class that
 * reads parameters from the Parameter Store component of the AWS Systems
 * Manager.</p>
 */
public interface ParameterStoreReader {
    /**
     * Get the name of the Short URL Mapping table in the DynamoDB
     * database.
     *
     * @return The name of the Short URL Mapping table in the DynamoDB
     * database.
     */
    String getShortUrlMappingTableName();
}
