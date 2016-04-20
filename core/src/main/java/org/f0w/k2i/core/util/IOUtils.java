package org.f0w.k2i.core.util;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.f0w.k2i.core.model.entity.Movie;
import org.f0w.k2i.core.util.exception.KinopoiskToIMDBException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.f0w.k2i.core.util.MovieUtils.*;

/**
 * I/O utils.
 */
public final class IOUtils {
    private IOUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Check that file is not null, exists and is not a directory or symlink
     * @param file File to check
     * @throws NullPointerException If file is null
     * @throws IllegalArgumentException If file not exists or not a file
     * @return Checked file
     */
    public static File checkFile(File file) {
        requireNonNull(file, "File is null!");
        checkArgument(file.exists(), "File not exists!");
        checkArgument(file.isFile(), "Not a file!");

        return file;
    }

    /**
     * Computes and returns file hashcode
     * @param file File to hash
     * @return File hashcode
     * @throws KinopoiskToIMDBException If an I/O error occurs
     */
    public static String getHashCode(File file) {
        try {
            return Files.hash(file, Hashing.sha256()).toString();
        } catch (IOException e) {
            throw new KinopoiskToIMDBException(e);
        }
    }

    /**
     * Parses movies from file
     * @param file File to parse
     * @return List of parsed movies
     * @throws KinopoiskToIMDBException If an I/O error occurs
     */
    public static List<Movie> parseMovies(File file) {
        try {
            Document document = Jsoup.parse(file, Charset.forName("windows-1251").toString());
            Elements content = document.select("table tr");

            List<String> header = content.remove(0)
                    .getElementsByTag("td")
                    .stream()
                    .map(Element::text)
                    .collect(Collectors.toList());

            List<Map<String, String>> elements = content.stream()
                    .map(e -> e.getElementsByTag("td")
                            .stream()
                            .map(Element::text)
                            .collect(Collectors.toList())
                    )
                    .map(e -> CollectionUtils.combineLists(header, e))
                    .collect(Collectors.toList());

            return elements.stream()
                    .map(m -> new Movie(
                            parseTitle(m.get("оригинальное название"), m.get("русскоязычное название")),
                            parseYear(m.get("год")),
                            parseRating(m.get("моя оценка"))
                    ))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new KinopoiskToIMDBException(e);
        }
    }
}