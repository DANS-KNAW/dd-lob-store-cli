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
import nl.knaw.dans.lobstorecli.api.TransferStatusDto;
import nl.knaw.dans.lobstorecli.api.TransferStatusInfoDto;
import nl.knaw.dans.lobstorecli.client.DefaultApi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "get-transfer-by-hash", description = "Retrieve a list of transfer requests by hash")
@Slf4j
@RequiredArgsConstructor
public class GetTransferByHashCommand implements Callable<Integer> {

    @NonNull
    private final DefaultApi api;

    @Parameters(index = "0", description = "The SHA-1 hash of the file")
    private String hash;

    @Option(names = { "--status" }, description = "Filter by status")
    private TransferStatusDto status;

    @Option(names = { "--datastation" }, description = "Filter by datastation")
    private String datastation;

    @Override
    public Integer call() throws Exception {
        log.debug("Getting transfers for hash: {}, status: {}, datastation: {}", hash, status, datastation);
        List<TransferStatusInfoDto> response = api.getTransfersByHash(hash, status, datastation);

        if (response.isEmpty()) {
            System.err.println("No transfers found for the given hash");
            return 1;
        }

        for (TransferStatusInfoDto item : response) {
            System.out.println("ID: " + item.getId());
            System.out.println("Status: " + item.getStatus());
            System.out.println("---");
        }

        return 0;
    }
}
