package com.example.controller;

import com.example.domain.Film;
import com.example.service.FilmService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @QueryMapping
    public List<Film> getFilms() {
        return filmService.getAllFilms();
    }

    @QueryMapping
    public Film getFilm(@Argument int id) {
        return filmService.getFilmById(id);
    }

    @MutationMapping
    public Film saveFilm(@Argument String name) {
        return filmService.saveFilm(name);
    }
}
