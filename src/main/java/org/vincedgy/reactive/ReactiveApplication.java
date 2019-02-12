package org.vincedgy.reactive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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

        this.reservationRepository
                .deleteAll()
                .thenMany(
                    Flux.just("Josh", "Stephan", "Mark", "Jane", "Madhura", "Jennifer", "Vader", "Maroua")
                        .map(name -> new Reservation(null, name))
                        .flatMap(this.reservationRepository::save)
                    )
                .thenMany(this.reservationRepository.findAll())
                .subscribe(reservation -> System.out.println("reservation : " + reservation.toString()));


    }

}

@RestController
class ReservationRestController {
    private final ReservationRepository reservationRepository;

    @Autowired
    ReservationRestController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/reservations")
    Flux<Reservation> getAllReservations() {
        return reservationRepository.findAll();
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
