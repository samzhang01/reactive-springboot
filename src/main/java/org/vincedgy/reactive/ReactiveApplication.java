package org.vincedgy.reactive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@SpringBootApplication
public class ReactiveApplication {

    private final ReservationRepository reservationRepository;

    public ReactiveApplication(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void writeDataToMongoDB() throws Exception {

        // Creating dummy data in a Flux of String
        Flux<String> names = Flux.just("Josh", "Stephan", "Mark", "Jane", "Madhura", "Jennifer", "Vader", "Maroua");

        // Build a collection of Reservation based on the dummy Flux of String
        Flux<Reservation> reservations = names.map(name -> new Reservation(null, name));

        // Then save the collection with the reservationRepository
        Flux<Reservation> saved = reservations.flatMap(this.reservationRepository::save);

        // Start to process with publish
        saved.subscribe(reservation -> System.out.println("reservation : " + reservation.toString()));

    }

}

interface ReservationRepository extends ReactiveMongoRepository<Reservation, String> {
}

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
class Reservation {
    String id;
    String name;
}
