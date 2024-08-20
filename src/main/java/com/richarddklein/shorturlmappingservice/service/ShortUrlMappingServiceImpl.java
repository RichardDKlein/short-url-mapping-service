/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlmappingservice.service;

import java.util.Objects;

import com.richarddklein.shorturlcommonlibrary.aws.ParameterStoreAccessor;
import com.richarddklein.shorturlmappingservice.dao.ShortUrlMappingDao;
import com.richarddklein.shorturlmappingservice.dto.ShortUrlMappingStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
public class ShortUrlMappingServiceImpl implements ShortUrlMappingService {
    private final ShortUrlMappingDao shortUrlMappingDao;
    private final ParameterStoreAccessor parameterStoreAccessor;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    public ShortUrlMappingServiceImpl(
            ShortUrlMappingDao shortUrlMappingDao,
            ParameterStoreAccessor parameterStoreAccessor) {

        this.shortUrlMappingDao = shortUrlMappingDao;
        this.parameterStoreAccessor = parameterStoreAccessor;
    }

    // Initialization of the Short URL Mapping repository is performed rarely,
    // and then only by the Admin from a local machine. Therefore, we do not
    // need to use reactive (asynchronous) programming techniques here. Simple
    // synchronous logic will work just fine.
    @Override
    public ShortUrlMappingStatus
    initializeShortUrlMappingRepository(ServerHttpRequest request) {
        if (!isRunningLocally(Objects.requireNonNull(
                request.getRemoteAddress()).getHostString())) {

            return ShortUrlMappingStatus.NOT_ON_LOCAL_MACHINE;
        }

        shortUrlMappingDao.initializeShortUrlMappingRepository();
        return ShortUrlMappingStatus.SUCCESS;
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    /**
     * Is the service running locally?
     *
     * <p>Determine whether the Short URL User Service is running on your local
     * machine, or in the AWS cloud.</p>
     *
     * @param hostString The host that sent the HTTP request.
     * @return 'true' if the service is running locally, 'false' otherwise.
     */
    private boolean isRunningLocally(String hostString) {
        return hostString.contains("localhost");
    }
}
