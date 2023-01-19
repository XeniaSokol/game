package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/rest")
public class PlayerController {
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * Создание нового игрока
     *
     * @param player - новый игрок
     * @return результат создания нового игрока
     */
    @PostMapping(value = "/players")
    public ResponseEntity<Player> create(@RequestBody Player player) {
        if (!playerService.isValidPlayer(player)) {
            return new ResponseEntity<>(player, HttpStatus.BAD_REQUEST);
        }
        playerService.create(player);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    /**
     * Получение всех отфильтрованных и отсортиванных игроков на странице
     *
     * @param name          - имя игрока, которое ищем
     * @param title         - титул
     * @param race          - расса
     * @param profession    - профессия
     * @param after         - дата начала поиска по дате рождения
     * @param before        - дата окончания поиска по дате рождения
     * @param banned        - забаннен или нет
     * @param minExperience - минимальный опыт
     * @param maxExperience - максимальный опыт
     * @param minLevel      - минимиальный уровень
     * @param maxLevel      - максимальный уровень
     * @param order         - порядок сортировки
     * @param pageNumber    - номер страницы
     * @param pageSize      - количество записей на одной странице
     * @return список всех отстортированных и отфильтрованных игроков
     */
    @GetMapping(value = "/players")
    public ResponseEntity<List<Player>> readAll(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(value = "order", required = false) PlayerOrder order,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        final List<Player> filteredPlayers = playerService.filteredPlayers(name, title, race, profession, after,
                before, banned, minExperience, maxExperience, minLevel, maxLevel);
        final List<Player> sortedPlayers = playerService.sort(filteredPlayers, order);
        final List<Player> playersOnPage = playerService.getPage(sortedPlayers, pageNumber, pageSize);
        return new ResponseEntity<>(playersOnPage, HttpStatus.OK);
    }

    /**
     * Получение количества всех отфильтрованных игроков
     *
     * @param name          - имя игрока, котрое ищем
     * @param title         - титул игрока
     * @param race          - расса игрока
     * @param profession    - профессия игрока
     * @param after         - дата начала поиска по дате рождения
     * @param before        - дата окончания поиска по дате рождения
     * @param banned        - забаннен или нет
     * @param minExperience - минимальный опыт
     * @param maxExperience - максимальный опыт
     * @param minLevel      - минимиальный уровень
     * @param maxLevel      - максимальный уровень
     * @return количество всех отфилтрованных игроков
     */
    @GetMapping(value = "/players/count")
    public ResponseEntity<Integer> getCountPlayers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel
    ) {
        final Integer count = playerService.getCount(name, title, race, profession, after,
                before, banned, minExperience, maxExperience, minLevel, maxLevel);
        return count != null ?
                new ResponseEntity<>(count, HttpStatus.OK) :
                new ResponseEntity<>(count, HttpStatus.NOT_FOUND);
    }

    /**
     * Получение игрока по идентификатору
     *
     * @param id - идентификатор игрока для поиска
     * @return если все ок, возвращает найденного игрока
     */
    @GetMapping(value = "/players/{id}")
    public ResponseEntity<Player> read(@PathVariable(name = "id") Long id) {
        final Player player = playerService.read(id);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    /**
     * Обновление игрока
     *
     * @param id     - идентификатор игрока
     * @param player - игрок
     * @return если игрок обновлен, возвращает обновленного игрока
     */
    @PostMapping(value = "/players/{id}")
    public Player updatePlayer(@PathVariable Long id, @RequestBody Player player) {
        return playerService.update(player, id);
    }

    /**
     * Удаление игрока
     *
     * @param id - идентификатор игрока, которого необходимо удалить
     */
    @DeleteMapping(value = "/players/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        if (id == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        playerService.delete(id);
    }
}
