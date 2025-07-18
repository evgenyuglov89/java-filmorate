package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public List<Mpa> mpaList() {
        return mpaDbStorage.mpaList();
    }

    public Mpa findById(int id) {
        return mpaDbStorage.findById(id);
    }
}
