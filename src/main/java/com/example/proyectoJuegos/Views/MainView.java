package com.example.proyectoJuegos.Views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("") // Esta será la página principal (localhost:8080/)
@PermitAll // Indica que cualquier usuario logueado puede verla
public class MainView extends VerticalLayout {

    public MainView() {
        add(new H1("¡Bienvenido a Proyecto Juegos!"));
    }

}
