package com.landim.catalogProductions.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbResponseDto {

    @JsonProperty("results")
    private List<MovieDto> results;

    @Getter
    @Setter
    public static class MovieDto{

        @JsonAlias({"title", "name"})
        private String title;

        @JsonProperty("overview")
        private String overview;

        @JsonAlias({"release_date", "first_air_date"})
        private String releaseDate;

        @JsonProperty("vote_average")
        private Double voteAverage;

        @JsonProperty("popularity")
        private Double popularity;
    }
}