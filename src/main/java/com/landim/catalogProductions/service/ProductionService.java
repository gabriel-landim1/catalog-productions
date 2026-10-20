package com.landim.catalogProductions.service;

import com.landim.catalogProductions.dto.TmdbResponseDto;
import com.landim.catalogProductions.entity.Production;
import com.landim.catalogProductions.entity.Status;
import com.landim.catalogProductions.entity.Type;
import com.landim.catalogProductions.exception.ResourceAlreadyExistsException;
import com.landim.catalogProductions.exception.ResourceNotFoundException;
import com.landim.catalogProductions.repository.ProductionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProductionService {

    private final ProductionRepository productionRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    @Value("${tmdb.api.url}")
    private String tmdbApiUrl;

    public ProductionService(ProductionRepository productionRepository) {
        this.productionRepository = productionRepository;
    }

    public List<Production> findAll(Type type, Double grade, String title){
        if (title != null && !title.trim().isEmpty()){
            return productionRepository.findByTitleContainingIgnoreCase(title);
        }
        if (type != null && grade != null){
            return productionRepository.findByTypeAndGradeGreaterThanEqual(type, grade);
        }
        if (type != null){
            return productionRepository.findByType(type);
        }
        if (grade != null){
            return productionRepository.findByGradeGreaterThanEqual(grade);
        }
        return productionRepository.findAll();
    }

    public Production findById(String id){
        return productionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    public Production insert(Production obj){
        return productionRepository.save(obj);
    }

    public Production update(Production obj, String id){
        findById(id);
        obj.setId(id);
        return productionRepository.save(obj);
    }

    public void delete(String id){
        findById(id);
        productionRepository.deleteById(id);
    }

    @Cacheable(value = "tmdbCache", key = "#movieName")
    public Production importAndSaveFromTmdb(String movieName){

        System.out.println(">>> REQUISIÇÃO REAL BATEU NO TMDB!");

        String[] partes = movieName.split(" ");
        String resultado = "";

        for (int i = 0; i < partes.length; i++){
            resultado += partes[i].substring(0, 1).toUpperCase() + partes[i].substring(1);
            if (i < partes.length -1){
                resultado += " ";
            }
        }

        System.out.println("Resultado: " + resultado);

        // 1. Busca nos filmes
        String movieUrl = tmdbApiUrl + "/search/movie?api_key=" + tmdbApiKey + "&query=" + resultado;
        TmdbResponseDto movieResponse = restTemplate.getForObject(movieUrl, TmdbResponseDto.class);

        TmdbResponseDto.MovieDto bestMovie = null;
        double moviePopularity = 0;

        if (movieResponse != null && movieResponse.getResults() != null && !movieResponse.getResults().isEmpty()){
            bestMovie = movieResponse.getResults().get(0);
            if (bestMovie.getPopularity() != null){
                moviePopularity = bestMovie.getPopularity();
            }
        }

        // 2. Busca nas series
        String tvUrl = tmdbApiUrl + "/search/tv?api_key=" + tmdbApiKey + "&query=" + resultado;
        TmdbResponseDto tvResponse = restTemplate.getForObject(tvUrl, TmdbResponseDto.class);

        TmdbResponseDto.MovieDto bestTv = null;
        double tvPopularity = 0;

        if (tvResponse != null && tvResponse.getResults() != null && !tvResponse.getResults().isEmpty()){
            bestTv = tvResponse.getResults().get(0);
            if (bestTv.getPopularity() != null){
                tvPopularity = bestTv.getPopularity();
            }
        }

        if (bestTv == null && bestMovie == null){
            throw new ResourceNotFoundException("Produção nao encontrado na base do TMDB!");
        }

        if (bestMovie == null) {
            // Se o filme é nulo, com certeza a série existe (por causa do IF de cima)
            return convertToProduction(bestTv, Type.SERIES);
        }

        if (bestTv == null) {
            // Se a série é nulo, com certeza o filme existe
            return convertToProduction(bestMovie, Type.MOVIE);
        }

        // Se o código chegou aqui, NENHUM dos dois é nulo!
        if (tvPopularity > moviePopularity){
            return convertToProduction(bestTv, Type.SERIES);
        } else {
            return convertToProduction(bestMovie, Type.MOVIE);
        }
    }

    public Production convertToProduction(TmdbResponseDto.MovieDto movieDto, Type type){
        Production production = new Production();

        if (productionRepository.existsByTitle(movieDto.getTitle())){
            throw new ResourceAlreadyExistsException("A producao " + movieDto.getTitle() + " ja esta cadastrada no catalogo!");
        }

        production.setTitle(movieDto.getTitle());
        production.setReview(movieDto.getOverview());

        if (movieDto.getReleaseDate() != null && movieDto.getReleaseDate().length() > 4){
            production.setYearRelease(Integer.parseInt(movieDto.getReleaseDate().substring(0, 4)));
        }

        production.setType(type);
        production.setGrade(movieDto.getVoteAverage());
        production.setStatus(Status.QUERO_ASSISTIR);

        return productionRepository.save(production);
    }
}