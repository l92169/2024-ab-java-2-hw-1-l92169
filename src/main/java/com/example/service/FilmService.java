package com.example.service;

import com.example.domain.Film;
import com.example.repository.FilmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Film getFilmById(int id) {
        return filmRepository.findById(id).orElse(null);
    }

    public Film saveFilm(String name) {
        Film film = new Film(name);
        return filmRepository.save(film);
    }
}
