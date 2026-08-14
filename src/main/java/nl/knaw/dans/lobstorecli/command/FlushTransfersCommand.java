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
import nl.knaw.dans.lobstorecli.client.ApiException;
import nl.knaw.dans.lobstorecli.client.DefaultApi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "flush-transfers", description = "Forces a packaging task for downloaded files for the given datastation")
@Slf4j
@RequiredArgsConstructor
public class FlushTransfersCommand implements Callable<Integer> {

    @NonNull
    private final DefaultApi api;

    @Parameters(index = "0", description = "The name of the datastation to flush transfers for")
    private String datastation;

    @Override
    public Integer call() throws Exception {
        try {
            log.debug("Flushing transfers for datastation: {}", datastation);
            api.flushTransfersWithHttpInfo(datastation);
            System.out.println("Transfers for datastation " + datastation + " successfully flushed.");
            return 0;
        }
        catch (ApiException e) {
            if (e.getCode() == 204) {
                System.out.println("No pending transfers found for datastation " + datastation + ".");
                return 0;
            }
            log.error("Failed to flush transfers", e);
            System.err.println("Failed to flush transfers: " + e.getMessage());
            return 1;
        }
    }
}
