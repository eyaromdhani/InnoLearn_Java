package org.example;

import org.example.entity.Event;
import org.example.service.EventService;
import org.example.utils.Mydatabase;

import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Test de la connexion");
        Mydatabase.getInstance();
        
        EventService eventService = new EventService();
        
        System.out.println("\n test ajout ");
        Event nouvelEvent = new Event(
                "Conférence Tech 2026",
                "Une conférence sur les nouvelles technologies et l'IA.",
                "Conférence",
                LocalDateTime.of(2026, 5, 20, 9, 0),
                LocalDateTime.of(2026, 5, 20, 17, 0),
                "Tunis",
                150,
                "Planifié"
        );
        
        eventService.addEvent(nouvelEvent);
        
        System.out.println("\n--- Affichage des événements après l'ajout ---");
        List<Event> events = eventService.getAllEvents();
        
        if (events.isEmpty()) {
            System.out.println("La base de données ne contient aucun événement !");
        } else {
            for (Event e : events) {
                System.out.println(e);
            }
        }
    }
}
