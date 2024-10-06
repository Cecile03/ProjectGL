package com.example.backend.service;
import com.example.backend.dto.StudentDTO;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * Service de traitement des fichiers CSV.
 */
@Service
public class CsvProcessingService {

    /**
     * Traite un fichier CSV.
     *
     * @param file Le fichier CSV à traiter.
     * @return La liste des étudiants extraits du fichier CSV.
     * @throws IOException Si une erreur survient lors du traitement du fichier CSV.
     */
    public List<StudentDTO> processCsvFile(MultipartFile file) throws IOException {
        try (Reader reader = new InputStreamReader(file.getInputStream())) {

            return new CsvToBeanBuilder<StudentDTO>(reader)
                    .withType(StudentDTO.class)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .build()
                    .parse();
        }
        catch (IOException e) {
            throw new IOException("Error while processing CSV file: " + e.getMessage());
        }
    }
}