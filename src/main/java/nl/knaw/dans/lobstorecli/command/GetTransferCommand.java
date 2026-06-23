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
import nl.knaw.dans.lobstorecli.api.TransferStatusInfoDto;
import nl.knaw.dans.lobstorecli.client.ApiException;
import nl.knaw.dans.lobstorecli.client.DefaultApi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.UUID;
import java.util.concurrent.Callable;

@Command(name = "get-transfer", description = "Get the status of a file download request")
@Slf4j
@RequiredArgsConstructor
public class GetTransferCommand implements Callable<Integer> {

    @NonNull
    private final DefaultApi api;

    @Parameters(index = "0", description = "The ID of the transfer")
    private UUID id;

    @Override
    public Integer call() throws Exception {
        try {
            log.debug("Getting transfer status for ID: {}", id);
            TransferStatusInfoDto response = api.getTransferStatus(id);
            System.out.println("ID: " + response.getId());
            System.out.println("Status: " + response.getStatus());
            return 0;
        }
        catch (ApiException e) {
            if (e.getCode() == 404) {
                System.err.println("Transfer not found: " + id);
                return 1;
            }
            else {
                throw e;
            }
        }
    }
}
