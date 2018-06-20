package org.superbiz.moviefun.albums;


//import sun.misc.IOUtils;

//import org.apache.tomcat.util.http.fileupload.IOUtils;

import org.apache.tika.Tika;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

public class FileStore implements BlobStore {
    private Tika tika = new Tika();

    @Override
    public void put(Blob blob) throws IOException {
        File file = new File("covers/"+blob.getName());
        file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();
        try (FileOutputStream outputStream = new FileOutputStream((file))){
            IOUtils.copy(blob.getInputStream(), outputStream);
        }
        // ...
    }


    @Override
    public Optional<Blob> get(String name) throws IOException {
        File file = new File("covers/"+name);
        if (!file.exists()){
            return Optional.empty();
        }
        // ...
        return Optional.of(
                new Blob(name,new FileInputStream(file),tika.detect(file)));
    }

    @Override
    public void deleteAll() {
        // ...
    }
}