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
import nl.knaw.dans.lobstorecli.api.TransferRequestDto;
import nl.knaw.dans.lobstorecli.api.TransferResponseDto;
import nl.knaw.dans.lobstorecli.client.ApiException;
import nl.knaw.dans.lobstorecli.client.DefaultApi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "add", description = "Add a file download request to the lob-store")
@Slf4j
@RequiredArgsConstructor
public class AddCommand implements Callable<Integer> {

    @NonNull
    private final DefaultApi api;

    @Option(names = { "--sha1" }, description = "SHA-1 checksum of the file", required = true)
    private String sha1;

    @Option(names = { "--datastation" }, description = "Shortname of the datastation", required = true)
    private String datastation;

    @Parameters(index = "0", description = "File ID to download")
    private Long fileId;

    @Override
    public Integer call() throws Exception {
        try {
            TransferRequestDto request = new TransferRequestDto();
            request.setDataverseFileId(fileId);
            request.setDatastation(datastation);
            request.setSha1Sum(sha1);

            log.debug("Sending add transfer request: {}", request);
            TransferResponseDto response = api.addTransfer(request);
            System.out.println("Transfer added with ID: " + response.getId());

        }
        catch (ApiException e) {
            if (e.getCode() == 303) {
                String location = e.getResponseHeaders().get("Location").get(0);
                System.out.println("File already in LOB store: " + location);
            }
            else {
                throw e;
            }
        }
        return 0;
    }
}
