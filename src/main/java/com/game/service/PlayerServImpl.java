package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Repository
@Transactional
public class PlayerServImpl implements PlayerService {

    @PersistenceContext
    private EntityManager manager;
    private final PlayerRepository playerRepository;

    public PlayerServImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public Player create(Player player) {
        player.setLevel(updateLevel(player.getExperience()));
        player.setUntilNextLevel(updateUntilNextLevel(player.getLevel(), player.getExperience()));
        if (player.isBanned() == null) player.setBanned(false);
        return playerRepository.save(player);
    }

    @Override
    public Player update(Player player, Long id) {
        Player playerOld = read(id);
        if (player.getName() != null) playerOld.setName(player.getName());
        if (player.getTitle() != null) playerOld.setTitle(player.getTitle());
        if (player.getRace() != null) playerOld.setRace(player.getRace());
        if (player.getProfession() != null) playerOld.setProfession(player.getProfession());
        if (player.isBanned() != null) playerOld.setBanned(player.isBanned());

        if (player.getBirthday() != null) {
            if (!isBirthdayValid(player.getBirthday()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            playerOld.setBirthday(player.getBirthday());
        }

        if (player.getExperience() != null) {
            if (!isExperienceValid(player.getExperience()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            playerOld.setExperience(player.getExperience());
            playerOld.setLevel(updateLevel(playerOld.getExperience()));
            playerOld.setUntilNextLevel(updateUntilNextLevel(playerOld.getLevel(), playerOld.getExperience()));
        }

        return playerRepository.save(playerOld);
    }


    @Override
    public void delete(Long id) {
        Player player = read(id);
        manager.remove(player);
    }


    @Override
    public Player read(Long id) {
        if (id <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Player player = manager.find(Player.class, id);
        if (player == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return player;
    }

    @Override
    public List<Player> readAll() {
        return manager.createQuery("from Player", Player.class).getResultList();
    }

    @Override
    public List<Player> filteredPlayers(String name, String title,
                                        Race race, Profession profession, Long after, Long before,
                                        Boolean banned, Integer minExperience, Integer maxExperience,
                                        Integer minLevel, Integer maxLevel) {
        List<Player> players = readAll();
        List<Player> result = new ArrayList<>();
        final Date afterDate = after == null ? null : new Date(after);
        final Date beforeDate = before == null ? null : new Date(before);

        players.forEach(player -> {
            if (name != null && !player.getName().contains(name)) return;
            if (title != null && !player.getTitle().contains(title)) return;
            if (race != null && player.getRace() != race) return;
            if (profession != null && player.getProfession() != profession) return;
            if (after != null && player.getBirthday().before(afterDate)) return;
            if (before != null && player.getBirthday().after(beforeDate)) return;
            if (banned != null && player.isBanned() != banned) return;
            if (minExperience != null && player.getExperience().compareTo(minExperience) < 0) return;
            if (maxExperience != null && player.getExperience().compareTo(maxExperience) > 0) return;
            if (minLevel != null && player.getLevel().compareTo(minLevel) < 0) return;
            if (maxLevel != null && player.getLevel().compareTo(maxLevel) > 0) return;
            result.add(player);
        });
        return result;
    }

    @Override
    public List<Player> sort(List<Player> players, PlayerOrder order) {
        if (order != null) {
            players.sort((player1, player2) -> {
                switch (order) {
                    case ID:
                        return player1.getId().compareTo(player2.getId());
                    case NAME:
                        return player1.getName().compareTo(player2.getName());
                    case EXPERIENCE:
                        return player1.getExperience().compareTo(player2.getExperience());
                    case BIRTHDAY:
                        return player1.getBirthday().compareTo(player2.getBirthday());
                    case LEVEL:
                        return player1.getLevel().compareTo(player2.getLevel());
                    default:
                        return 0;
                }
            });
        }
        return players;
    }

    @Override
    public List<Player> getPage(List<Player> players, Integer pageNumber, Integer pageSize) {
        final int page = pageNumber == null ? 0 : pageNumber;
        final int size = pageSize == null ? 3 : pageSize;
        final int from = page * size;
        int to = from + size;
        if (to > players.size()) to = players.size();
        return players.subList(from, to);
    }

    @Override
    public Integer getCount(String name, String title, Race race, Profession profession,
                            Long after, Long before, Boolean banned, Integer minExperience,
                            Integer maxExperience, Integer minLevel, Integer maxLevel) {
        return filteredPlayers(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel).size();
    }

    @Override
    public Boolean isValidPlayer(Player player) {
        return player != null && isNameValid(player.getName())
                && isTitleValid(player.getTitle())
                && player.getRace() != null
                && player.getProfession() != null
                && isBirthdayValid(player.getBirthday())
                && isExperienceValid(player.getExperience());
    }

    /**
     * Проверка на валидность имени
     *
     * @param text - строка
     * @return возвращает true если имя заполнен
     */
    private boolean isNameValid(String text) {
        return text != null && !text.isEmpty() && text.length() <= 12;
    }

    /**
     * Проверка на валидность титула
     *
     * @param text - строка
     * @return возвращает true если титул заполнен
     */
    private boolean isTitleValid(String text) {
        return text != null && !text.isEmpty() && text.length() <= 30;
    }

    /**
     * Проверка на валидность даты
     *
     * @param birthday - дата рождения
     * @return возвращает true если дата начала больше или равна 2000 года и дата окончания меньше или равна 3000 году
     */
    private boolean isBirthdayValid(Date birthday) {
        Calendar after = Calendar.getInstance();
        after.set(Calendar.YEAR, 2000);
        Calendar before = Calendar.getInstance();
        before.set(Calendar.YEAR, 3000);
        return birthday != null && birthday.getTime() > after.getTimeInMillis()
                && birthday.getTime() < before.getTimeInMillis()
                && birthday.getTime() > 0;
    }

    /**
     * Проверка валидности опыта
     *
     * @param experience - опыт
     * @return возвращает true если опыт больше 0, но меньше 10 млн
     */
    private boolean isExperienceValid(Integer experience) {
        return experience != null && experience >= 0 && experience <= 10_000_000;
    }

    /**
     * Обновление уровня игрока
     *
     * @param experience - опыт игрока
     * @return новый уровень игрока
     */
    private int updateLevel(Integer experience) {
        return (((int) Math.sqrt(2500 + 200 * experience)) - 50) / 100;
    }

    /**
     * Обновление остатка опыта до следующего уровня
     *
     * @param level      - уровень игрока
     * @param experience - опыт игрока
     * @return новый остаток опыта для следующего уровня
     */
    private int updateUntilNextLevel(Integer level, Integer experience) {
        return 50 * (level + 1) * (level + 2) - experience;
    }
}
