package org.dkn.cartrello.repository;

import org.dkn.cartrello.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    // @Query - Анотация, която дефинира персонализирана JPQL заявка (Java Persistence Query Language)
    // Тази заявка избира всички обекти Car (c) от базата, които отговарят на зададените филтри.
    // Във WHERE клаузата се използват параметри, които се подават към метода.
    @Query("SELECT c FROM Car c " +
            // Ако параметърът :brand е null, този филтър се игнорира (всеки запис минава).
            // Ако не е null, филтрираме по марка на колата (case insensitive чрез LOWER)
            "WHERE (:brand IS NULL OR LOWER(c.brand) = LOWER(:brand)) " +

            // Филтрираме по модел, пак с проверка за null и без значение от главни/малки букви
            "AND (:model IS NULL OR LOWER(c.model) = LOWER(:model)) " +

            // Филтър по година - ако годината е null, игнорира се, иначе се търси точно съвпадение
            "AND (:year IS NULL OR c.year = :year) " +

            // Филтър по собственик (username) с игнориране на null и case insensitive сравнение
            "AND (:owner IS NULL OR LOWER(c.ownerUsername) = LOWER(:owner))")

    // Методът searchCars приема параметри за търсене: brand, model, year и owner.
    // @Param("...") анотацията свързва параметрите на метода с имената в заявката (@Query).
    // Това гарантира, че стойностите се поставят на правилните места в JPQL заявката.
    List<Car> searchCars(@Param("brand") String brand,
                         @Param("model") String model,
                         @Param("year") Integer year,
                         @Param("owner") String owner);
}
