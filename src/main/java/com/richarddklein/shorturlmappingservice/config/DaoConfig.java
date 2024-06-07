/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.config;

import com.richarddklein.shorturlcommonlibrary.aws.ParameterStoreReader;
import com.richarddklein.shorturlcommonlibrary.config.AwsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Import;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import com.richarddklein.shorturlmappingservice.dao.ShortUrlMappingDao;
import com.richarddklein.shorturlmappingservice.dao.ShortUrlMappingDaoImpl;
import com.richarddklein.shorturlmappingservice.entity.ShortUrlMapping;

/**
 * The DAO (Data Access Object) @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the DAO package.</p>
 */
@Configuration
@Import(AwsConfig.class)
public class DaoConfig {
    @Autowired
    ParameterStoreReader parameterStoreReader;

    @Bean
    public ShortUrlMappingDao
    shortUrlMappingDao() {
        return new ShortUrlMappingDaoImpl(
                parameterStoreReader,
                dynamoDbClient(),
                dynamoDbEnhancedClient(),
                shortUrlMappingTable()
        );
    }

    @Bean
    public DynamoDbClient
    dynamoDbClient() {
        return DynamoDbClient.builder()
                .credentialsProvider(DefaultCredentialsProvider
                        .create())
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient
    dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient())
                .build();
    }

    @Bean
    public DynamoDbTable<ShortUrlMapping>
    shortUrlMappingTable() {
        return dynamoDbEnhancedClient().table(
                parameterStoreReader.getShortUrlMappingTableName(),
                TableSchema.fromBean(ShortUrlMapping.class));
    }
}
