package bel.dev.sa_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import bel.dev.sa_backend.Enums.TypeSentiment;
import bel.dev.sa_backend.entities.Sentiment;

public interface SentimentRepository extends JpaRepository<Sentiment, Integer> {

    List<Sentiment> findByType(TypeSentiment type);

}
