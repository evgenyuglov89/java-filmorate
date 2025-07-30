package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorDbStorage directorDbStorage;

    public DirectorService(DirectorDbStorage directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }

    public List<Director> directorsList(){
        return directorDbStorage.direcrorsList();
    }

    public Director findById(int id){
        return directorDbStorage.findById(id);
    }

    public Director createDirector(Director director){
        return directorDbStorage.createDirector(director);
    }

    public Director updateDirector(Director director){
        return directorDbStorage.updateDirector(director);
    }

    public void removeDirector(int id){
        directorDbStorage.removeDirector(id);
    }
}
