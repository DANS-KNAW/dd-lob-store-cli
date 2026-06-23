/*
 * Copyright (C) 2026 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.lobstorecli.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.knaw.dans.lobstorecli.api.LocationResponseDto;
import nl.knaw.dans.lobstorecli.client.ApiException;
import nl.knaw.dans.lobstorecli.client.DefaultApi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "get-location", description = "Retrieve the location of a file by its SHA-1 hash")
@Slf4j
@RequiredArgsConstructor
public class GetLocationCommand implements Callable<Integer> {

    @NonNull
    private final DefaultApi api;

    @Option(names = { "--store" }, description = "The name of the datastation whose LOB store to query", required = true)
    private String store;

    @Parameters(index = "0", description = "The SHA-1 hash of the file")
    private String hash;

    @Override
    public Integer call() throws Exception {
        try {
            log.debug("Getting location for store: {}, hash: {}", store, hash);
            LocationResponseDto response = api.getLocationByHash(store, hash);
            System.out.println("Store: " + response.getDatastation());
            System.out.println("Bucket: " + response.getBucket());
            return 0;
        }
        catch (ApiException e) {
            if (e.getCode() == 404) {
                System.err.println("Location not found for the given hash in store: " + store);
                return 1;
            }
            else {
                throw e;
            }
        }
    }
}
