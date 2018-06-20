package org.superbiz.moviefun.albums;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType.NUMBER;
import static org.superbiz.moviefun.CsvUtils.readFromCsv;

@Component
public class AlbumFixtures {

    private final ObjectReader objectReader;

    public AlbumFixtures() {
        CsvSchema schema = CsvSchema.builder()
            .addColumn("artist")
            .addColumn("title")
            .addColumn("year", NUMBER)
            .addColumn("rating", NUMBER)
            .build();

        objectReader = new CsvMapper().readerFor(Album.class).with(schema);
    }

    public List<Album> load() {
        return readFromCsv(objectReader, this.getClass().getClassLoader().getResourceAsStream("album-fixtures.csv"));
    }
}
