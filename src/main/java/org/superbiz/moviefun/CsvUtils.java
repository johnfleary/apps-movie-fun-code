package org.superbiz.moviefun;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CsvUtils {

    public static String readFile(InputStream inputStream) {



           Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return "";
            }


    }

    public static <T> List<T> readFromCsv(ObjectReader objectReader, InputStream inputStream) {
        try {
            List<T> results = new ArrayList<>();

            MappingIterator<T> iterator = objectReader.readValues(readFile(inputStream));

            while (iterator.hasNext()) {
                results.add(iterator.nextValue());
            }

            return results;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
