package org.f0w.k2i.core.model.repository;

import com.google.inject.persist.Transactional;
import org.f0w.k2i.core.model.entity.ImportProgress;

import com.google.inject.Inject;
import org.f0w.k2i.core.model.entity.KinopoiskFile;
import org.f0w.k2i.core.model.entity.Movie;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class ImportProgressRepositoryImpl implements ImportProgressRepository {
    @Inject
    private EntityManager em;

    @Override
    @Transactional
    public ImportProgress save(ImportProgress importProgress) {
        em.persist(importProgress);

        return importProgress;
    }

    @Override
    public void saveAll(KinopoiskFile kinopoiskFile, List<Movie> movies) {
        movies.forEach(m -> save(new ImportProgress(kinopoiskFile, m, false, false)));
    }

    @Override
    public List<ImportProgress> findNotImportedByFile(KinopoiskFile kinopoiskFile) {
        TypedQuery<ImportProgress> query = em.createQuery(
                "FROM ImportProgress WHERE imported = :imported AND kinopoiskFileId = :kinopoiskFileId",
                ImportProgress.class
        );
        query.setParameter("imported", false);
        query.setParameter("kinopoiskFileId", kinopoiskFile.getId());

        return query.getResultList();
    }

    @Override
    public List<ImportProgress> findNotRatedByFile(KinopoiskFile kinopoiskFile) {
        TypedQuery<ImportProgress> query = em.createQuery(
                "FROM ImportProgress WHERE rated = :rated AND kinopoiskFileId = :kinopoiskFileId",
                ImportProgress.class
        );
        query.setParameter("rated", false);
        query.setParameter("kinopoiskFileId", kinopoiskFile.getId());

        return query.getResultList();
    }
}
