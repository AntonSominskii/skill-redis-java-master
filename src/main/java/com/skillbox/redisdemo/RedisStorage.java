package com.skillbox.redisdemo;

import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.config.Config;
import static java.lang.System.out;

public class RedisStorage {

    // Объект для работы с Redis
    private RedissonClient redisson;

    // Объект для работы с ключами
    private RKeys rKeys;

    // Объект для работы с Sorted Set'ом
    private RScoredSortedSet<String> onlineUsers;

    private final static String KEY = "ONLINE_USERS";

    // Инициализация
    void init() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        try {
            redisson = Redisson.create(config);
        } catch (RedisConnectionException Exc) {
            out.println("Не удалось подключиться к Redis");
            out.println(Exc.getMessage());
        }
        rKeys = redisson.getKeys();
        onlineUsers = redisson.getScoredSortedSet(KEY);
        rKeys.delete(KEY);
    }

    // Фиксирует посещение пользователем страницы
    void logPageVisit(int user_id) {
        // Добавляем пользователя в Sorted Set с временной меткой в качестве счётчика
        onlineUsers.add(System.currentTimeMillis() / 1000.0, String.valueOf(user_id));
    }

    // Метод для получения первого пользователя из очереди и перемещения его в конец
    public String showNextUser() {
        String firstUserId = onlineUsers.first();
        if (firstUserId != null) {
            onlineUsers.remove(firstUserId); // Удаляем пользователя из начала очереди
            onlineUsers.add(System.currentTimeMillis() / 1000.0, firstUserId); // Добавляем его обратно с текущим временем
        }
        return firstUserId;
    }

    // Метод для перемещения пользователя в начало очереди
    public void moveUserToFront(int userId) {
        double score = System.currentTimeMillis() / 1000.0 - 1000000;
        onlineUsers.add(score, String.valueOf(userId));
    }

    // Завершение работы
    void shutdown() {
        redisson.shutdown();
    }
}