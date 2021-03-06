package org.f0w.k2i.core.comparator;

import lombok.NonNull;
import org.f0w.k2i.core.model.entity.Movie;

/**
 * Checks that one movie title string contains another movie title string at the end.
 */
final class EndsWithTitleComparator extends AbstractMovieComparator {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean areEqual(@NonNull Movie movie1, @NonNull Movie movie2) {
        String title1 = movie1.getTitle().toLowerCase();
        String title2 = movie2.getTitle().toLowerCase();

        boolean result = title1.endsWith(title2) || title2.endsWith(title1);

        LOG.debug("Comparing title '{}' with title '{}', matches = '{}'", title1, title2, result);

        return result;
    }
}