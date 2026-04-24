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
import nl.knaw.dans.lobstorecli.api.TransferResponseItemDto;
import nl.knaw.dans.lobstorecli.client.ApiException;
import nl.knaw.dans.lobstorecli.client.DefaultApi;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "add", description = "Add a file download request to the lob-store")
@Slf4j
@RequiredArgsConstructor
public class AddCommand implements Callable<Integer> {

    @NonNull
    private final DefaultApi api;

    @ArgGroup(multiplicity = "1")
    private ExclusiveOptions exclusiveOptions;

    private static class ExclusiveOptions {
        @ArgGroup(exclusive = false, heading = "Single transfer options:%n")
        private SingleTransferOptions single;

        @Option(names = { "--input-file", "-i" }, description = "CSV file with columns DATASTATION, SHA1 and FILEID", required = true)
        private Path inputFile;
    }

    private static class SingleTransferOptions {
        @Option(names = { "--sha1" }, description = "SHA-1 checksum of the file", required = true)
        private String sha1;

        @Option(names = { "--datastation" }, description = "Shortname of the datastation", required = true)
        private String datastation;

        @Parameters(index = "0", description = "File ID to download")
        private Long fileId;
    }

    @Override
    public Integer call() throws Exception {
        List<TransferRequestDto> requests = new ArrayList<>();

        if (exclusiveOptions.inputFile != null) {
            try (Reader in = new FileReader(exclusiveOptions.inputFile.toFile())) {
                CSVParser parser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .build()
                    .parse(in);

                for (CSVRecord record : parser) {
                    TransferRequestDto request = new TransferRequestDto();
                    request.setDataverseFileId(Long.parseLong(record.get("FILEID")));
                    request.setDatastation(record.get("DATASTATION"));
                    request.setSha1Sum(record.get("SHA1"));
                    requests.add(request);
                }
            }
        }
        else {
            TransferRequestDto request = new TransferRequestDto();
            request.setDataverseFileId(exclusiveOptions.single.fileId);
            request.setDatastation(exclusiveOptions.single.datastation);
            request.setSha1Sum(exclusiveOptions.single.sha1);
            requests.add(request);
        }

        log.debug("Sending add transfers request with {} items", requests.size());
        List<TransferResponseItemDto> responses = api.addTransfers(requests);

        for (int i = 0; i < requests.size(); i++) {
            TransferRequestDto request = requests.get(i);
            TransferResponseItemDto response = responses.get(i);
            String label = requests.size() > 1 ? String.format("Item %d (%s/%d): ", i + 1, request.getDatastation(), request.getDataverseFileId()) : "";

            switch (response.getStatus()) {
                case 201:
                    System.out.println(label + "Transfer added with ID: " + response.getId());
                    break;
                case 303:
                    System.out.println(label + "File already in LOB store: " + response.getLocation());
                    break;
                case 409:
                    System.out.println(label + "Conflict: " + response.getMessage());
                    break;
                default:
                    System.out.println(label + "Error (Status " + response.getStatus() + "): " + response.getMessage());
            }
        }

        return 0;
    }
}
