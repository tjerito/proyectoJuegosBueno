package com.example.proyectoJuegos.Views;


import com.example.proyectoJuegos.Entities.Usuario;
import com.example.proyectoJuegos.Repositories.UsuarioRepositorio;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Route("register")
@PageTitle("Registro | GameHub")
@AnonymousAllowed
public class RegistrationView extends VerticalLayout {

    public RegistrationView(UsuarioRepositorio repository, PasswordEncoder encoder) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        getStyle().set("background", "url('https://i.pinimg.com/736x/39/54/7b/39547b886e2c518abe91199ce75d1bd7.jpg')")
                .set("background-size", "cover")
                .set("background-position", "center");

        H1 title = new H1("GAMEHUB");
        title.getStyle().set("color", "white").set("text-shadow", "2px 2px 4px #000");

        VerticalLayout container = new VerticalLayout();
        container.setWidth("400px");
        container.setPadding(true);
        container.setSpacing(true);
        container.setAlignItems(Alignment.STRETCH);

        container.getStyle()
                .set("background-color", "white") // Color sólido blanco
                .set("border-radius", "15px")
                .set("padding", "40px")
                .set("box-shadow", "0 10px 25px rgba(0,0,0,0.5)");

        H2 subtitle = new H2("NUEVO JUGADOR");
        subtitle.getStyle()
                .set("color", "black") // Texto negro
                .set("margin-top", "0")
                .set("text-align", "center");

        TextField username = new TextField("Elige tu alias");
        TextField email = new TextField("Tu correo electrónico");
        PasswordField password = new PasswordField("Tu contraseña secreta");

        String blackTextStyle = "black";
        username.getStyle().set("--lumo-body-text-color", blackTextStyle);
        username.getStyle().set("--lumo-secondary-text-color", blackTextStyle); // Para la etiqueta (Label)

        email.getStyle().set("--lumo-body-text-color", blackTextStyle);
        email.getStyle().set("--lumo-secondary-text-color", blackTextStyle);

        password.getStyle().set("--lumo-body-text-color", blackTextStyle);
        password.getStyle().set("--lumo-secondary-text-color", blackTextStyle);

        Button btnRegister = new Button("¡Registrarme!", VaadinIcon.GAMEPAD.create());
        btnRegister.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        btnRegister.getStyle().set("margin-top", "20px");

        Button btnBack = new Button("Volver al Login", e -> UI.getCurrent().navigate("login"));
        btnBack.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnBack.getStyle().set("color", "gray"); // Gris para que contraste con el blanco

        btnRegister.addClickListener(e -> {
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Notification.show("Todos los campos son obligatorios").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            try {
                Usuario user = new Usuario();
                user.setNombre(username.getValue());
                user.setEmail(email.getValue());
                user.setPassword(encoder.encode(password.getValue()));
                user.setFechaCreacion(LocalDateTime.now());
                repository.save(user);
                Notification.show("¡Cuenta creada!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                UI.getCurrent().navigate("login");
            } catch (Exception ex) {
                Notification.show("Error: El nombre o email ya existen").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        container.add(subtitle, username, email, password, btnRegister, btnBack);
        add(title, container);
    }
}