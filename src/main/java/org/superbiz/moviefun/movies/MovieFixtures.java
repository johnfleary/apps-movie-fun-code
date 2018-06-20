package org.superbiz.moviefun.movies;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType.NUMBER;
import static org.superbiz.moviefun.CsvUtils.readFromCsv;

@Component
public class MovieFixtures {

    private final ObjectReader objectReader;

    public MovieFixtures() {
        CsvSchema schema = CsvSchema.builder()
            .addColumn("title")
            .addColumn("director")
            .addColumn("genre")
            .addColumn("rating", NUMBER)
            .addColumn("year", NUMBER)
            .build();

        objectReader = new CsvMapper().readerFor(Movie.class).with(schema);
    }

    public List<Movie> load() {
        return readFromCsv(objectReader, this.getClass().getClassLoader().getResourceAsStream("movie-fixtures.csv"));
    }
}
