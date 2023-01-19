package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.List;

public interface PlayerService {
    /**
     * Получение всех игроков
     *
     * @return список всех игроков
     */
    List<Player> readAll();

    /**
     * Получение количества игроков
     *
     * @param name          - имя игрока
     * @param title         - титул
     * @param race          - расса игрока
     * @param profession    - профессия игрока
     * @param after         - дата начала поиска по дню рождения
     * @param before        - дата окончания поиска по дню рождения
     * @param banned        - забаннен или нет
     * @param minExperience - минимальный опыт
     * @param maxExperience - максимальный опыт
     * @param minLevel      - минимиальный уровень
     * @param maxLevel      - максимальный уровень
     * @return - возвращается количество игроков
     */
    Integer getCount(String name, String title, Race race, Profession profession,
                     Long after, Long before, Boolean banned, Integer minExperience,
                     Integer maxExperience, Integer minLevel, Integer maxLevel);

    /**
     * Создание нового игрока
     *
     * @param player - новый игрок
     * @return нового игрока
     */
    Player create(Player player);

    /**
     * Получение игрока по id
     *
     * @param id - идентификатор
     * @return возвращает игрока с указанным идентификатором
     */
    Player read(Long id);

    /**
     * Верно ли введены все данные
     *
     * @param player - текущий игрок
     * @return возвращает true если игрок соответсвует условиям
     */
    Boolean isValidPlayer(Player player);

    /**
     * Сортировка игроков по выбраному порядку
     *
     * @param players - список игроков
     * @param order   - порядок
     * @return возвращает отсортированный список игроков
     */
    List<Player> sort(List<Player> players, PlayerOrder order);

    /**
     * Получение списка игроков на одной странице
     *
     * @param players    - список игроков
     * @param pageNumber - номер страницы
     * @param pageSize   - размер страницы
     * @return возвращает список игроков на странице
     */
    List<Player> getPage(List<Player> players, Integer pageNumber, Integer pageSize);

    /**
     * Фильтрация игроков по выбранным параметрам
     *
     * @param name          - имя
     * @param title         - титул
     * @param race          - расса
     * @param profession    - профессия
     * @param after         - дата начала поиска по дню рождения
     * @param before        - дата окончания поиска по дню рождения
     * @param banned        - забаннен или нет
     * @param minExperience - минимальный опыт
     * @param maxExperience - максимальный опыт
     * @param minLevel      - минимиальный уровень
     * @param maxLevel      - максимальный уровень
     * @return - возвращается отфильтрованный список по выбранной характеристике
     */
    List<Player> filteredPlayers(String name, String title, Race race, Profession profession,
                                 Long after, Long before, Boolean banned, Integer minExperience,
                                 Integer maxExperience, Integer minLevel, Integer maxLevel);

    /**
     * Обновить игрока по id
     *
     * @param player - игрок
     * @param id     - идентификатор
     * @return возвращает true если данные игрока обновлены
     */
    Player update(Player player, Long id);

    /**
     * Удалить игрока по id
     *
     * @param id - идентификатор игрока
     */
    void delete(Long id);
}
