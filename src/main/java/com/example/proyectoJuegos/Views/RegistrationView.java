package com.example.proyectoJuegos.Views;


import com.example.proyectoJuegos.Entities.Usuario;
import com.example.proyectoJuegos.Repositories.UsuarioRepositorio;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

@Route("register")
@PageTitle("Registro | GameHub")
@AnonymousAllowed
public class RegistrationView extends VerticalLayout {

    public RegistrationView(UsuarioRepositorio repository, PasswordEncoder encoder) {
        // ... (configuración de estilo igual)

        TextField username = new TextField("Elige tu alias");
        TextField email = new TextField("Tu correo electrónico"); // NUEVO CAMPO
        PasswordField password = new PasswordField("Tu contraseña secreta");

        Button btnRegister = new Button("¡Registrarme!", VaadinIcon.GAMEPAD.create());
        btnRegister.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        btnRegister.addClickListener(e -> {
            // Validación básica para evitar el error del log
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Notification.show("Todos los campos son obligatorios").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                Usuario user = new Usuario();
                user.setNombre(username.getValue());
                user.setEmail(email.getValue()); // ASIGNAMOS EL EMAIL
                user.setPassword(encoder.encode(password.getValue()));

                repository.save(user);

                Notification.show("¡Cuenta creada!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                UI.getCurrent().navigate("login");
            } catch (Exception ex) {
                Notification.show("Error al guardar: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        // Añade el campo email al contenedor
        VerticalLayout container = new VerticalLayout(new H2("NUEVO JUGADOR"), username, email, password, btnRegister);
        // ... (resto del estilo)
        add(container);
    }
}