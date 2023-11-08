package com.skillbox.redisdemo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import static java.lang.System.out;

public class RedisTest {

    private static final SimpleDateFormat DF = new SimpleDateFormat("HH:mm:ss");

    private static void log(int UsersOnline) {
        out.println(String.format("[%s] Пользователей онлайн: %d", DF.format(new Date()), UsersOnline));
    }

    public static void main(String[] args) throws InterruptedException {
        RedisStorage redis = new RedisStorage();
        redis.init();

        Random random = new Random();

        // Заполняем Redis начальными данными о пользователях
        for (int userId = 1; userId <= 20; userId++) {
            redis.logPageVisit(userId);
            Thread.sleep(50); // Небольшая задержка
        }

        // Бесконечный цикл отображения пользователей
        while (true) {
            String nextUser = redis.showNextUser(); // Получаем следующего пользователя для отображения
            out.println("— На главной странице показываем пользователя " + nextUser);
            Thread.sleep(1000); // Ждем 1 секунду перед следующим показом

            // В одном из 10 случаев пользователь оплачивает услугу
            if (random.nextInt(10) == 0) {
                int luckyUserId = random.nextInt(20) + 1;
                redis.moveUserToFront(luckyUserId);
                out.println("> Пользователь " + luckyUserId + " оплатил платную услугу");
            }
        }

        // redis.shutdown(); Эта строка никогда не будет достигнута из-за бесконечного цикла
    }
}