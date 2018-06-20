package org.superbiz.moviefun.albums;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

@Controller
@RequestMapping("/albums")
public class AlbumsController {
    @Autowired
    public final BlobStore fileStore;

    private final AlbumsBean albumsBean;

    public AlbumsController(BlobStore fileStore, AlbumsBean albumsBean) {
        this.fileStore = fileStore;
        this.albumsBean = albumsBean;
    }


    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        saveUploadToFile(uploadedFile, getCoverFile(albumId));

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {
        Optional<Blob> optionalBlob = fileStore.get((String.valueOf(albumId)));
        if (optionalBlob.isPresent()) {
            byte[] imageBytes = IOUtils.toByteArray(optionalBlob.get().getInputStream());
            HttpHeaders headers = createImageHttpHeadersWithType(optionalBlob.get().getContentType(), imageBytes);
            return new HttpEntity<>(imageBytes, headers);
        } else {
            File defaultCover = new File(Thread.currentThread().getContextClassLoader().getResource("default-cover.jpg").toURI());
            InputStream in = new FileInputStream(defaultCover);
            byte[] imageBytes = IOUtils.toByteArray(in);
            Tika tika = new Tika();
            HttpHeaders headers = createImageHttpHeadersWithType(tika.detect(defaultCover), imageBytes);
            return new HttpEntity<>(imageBytes, headers);
        }
    }


    private void saveUploadToFile(@RequestParam("file") MultipartFile uploadedFile, File targetFile) throws IOException {
        Tika tika = new Tika();
        Blob blob = new Blob(targetFile.getName(),uploadedFile.getInputStream(),tika.detect(uploadedFile.getInputStream()));
        fileStore.put(blob);
/*        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(uploadedFile.getBytes());
        }*/
    }

    private HttpHeaders createImageHttpHeadersWithType(String contentType, byte[] imageBytes) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }

    private HttpHeaders createImageHttpHeaders(Path coverFilePath, byte[] imageBytes) throws IOException {
        String contentType = new Tika().detect(coverFilePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }

    private File getCoverFile(@PathVariable long albumId) {
        String coverFileName = format("covers/%d", albumId);
        return new File(coverFileName);
    }

    private Path getExistingCoverPath(@PathVariable long albumId) throws URISyntaxException {
        File coverFile = getCoverFile(albumId);
        Path coverFilePath;

        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {
            coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
        }

        return coverFilePath;
    }
}
